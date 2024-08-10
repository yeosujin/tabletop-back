package com.example.tabletop.commons.sse.service;

import com.example.tabletop.order.dto.KitchenOrderResponseDto;
import com.example.tabletop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {
    private final Map<Long, Sinks.Many<KitchenOrderResponseDto>> storeOrderSinks = new ConcurrentHashMap<>();

    public Flux<KitchenOrderResponseDto> getOrderStream(Long storeId) {
        return storeOrderSinks.computeIfAbsent(storeId, k -> Sinks.many().multicast().onBackpressureBuffer())
                .asFlux()
                .doOnNext(order -> log.info("Streaming order: storeId={}, order={}", storeId, order))
                .doOnComplete(() -> log.info("SSE connection completed for store ID: {}", storeId))
                .doOnCancel(() -> log.info("SSE connection cancelled for store ID: {}", storeId));
    }

    public Mono<Void> notifyNewOrder(Long storeId, KitchenOrderResponseDto order) {
        log.info("Notifying new order: storeId={}, order={}", storeId, order);
        return Mono.fromRunnable(() -> {
            Sinks.Many<KitchenOrderResponseDto> sink = storeOrderSinks.computeIfAbsent(storeId,
                    k -> Sinks.many().multicast().onBackpressureBuffer());
            Sinks.EmitResult result = sink.tryEmitNext(order);
            if (result.isFailure()) {
                log.error("Failed to emit order to sink: storeId={}, result={}", storeId, result);
            }
        });
    }

    public Mono<Void> removeOrderStream(Long storeId) {
        return Mono.fromRunnable(() -> {
            Sinks.Many<KitchenOrderResponseDto> sink = storeOrderSinks.remove(storeId);
            if (sink != null) {
                Sinks.EmitResult result = sink.tryEmitComplete();
                if (result.isFailure()) {
                    log.error("Failed to complete sink: storeId={}, result={}", storeId, result);
                }
                log.info("Removed order stream for store ID: {}", storeId);
            } else {
                log.warn("No order stream found for store ID: {}", storeId);
            }
        });
    }
}