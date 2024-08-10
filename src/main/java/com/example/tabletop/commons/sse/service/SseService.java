package com.example.tabletop.commons.sse.service;

import com.example.tabletop.order.dto.KitchenOrderResponseDto;
import com.example.tabletop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseService {
    private final Map<Long, Sinks.Many<ServerSentEvent<KitchenOrderResponseDto>>> storeOrderSinks = new ConcurrentHashMap<>();

    public Flux<ServerSentEvent<KitchenOrderResponseDto>> getOrderStream(Long storeId) {
        return storeOrderSinks.computeIfAbsent(storeId, k -> Sinks.many().multicast().onBackpressureBuffer())
                .asFlux()
                .doOnNext(event -> log.info("Streaming order: storeId={}, order={}", storeId, event.data()))
                .doOnComplete(() -> log.info("SSE connection completed for store ID: {}", storeId))
                .doOnCancel(() -> log.info("SSE connection cancelled for store ID: {}", storeId));
    }

    public Mono<Void> notifyNewOrder(Long storeId, KitchenOrderResponseDto order) {
        log.info("Notifying new order: storeId={}, order={}", storeId, order);
        return Mono.fromRunnable(() -> {
            Sinks.Many<ServerSentEvent<KitchenOrderResponseDto>> sink = storeOrderSinks.get(storeId);
            if (sink != null) {
                ServerSentEvent<KitchenOrderResponseDto> event = ServerSentEvent.<KitchenOrderResponseDto>builder()
                        .id(String.valueOf(order.getOrderId()))
                        .event("new-order")
                        .data(order)
                        .build();
                Sinks.EmitResult result = sink.tryEmitNext(event);
                if (result.isFailure()) {
                    log.error("Failed to emit order to sink: storeId={}, result={}", storeId, result);
                } else {
                    log.info("Successfully emitted new order event: storeId={}, orderId={}", storeId, order.getOrderId());
                }
            } else {
                log.warn("No active SSE connection found for store ID: {}", storeId);
            }
        });
    }

    public Mono<Void> removeOrderStream(Long storeId) {
        return Mono.fromRunnable(() -> {
            Sinks.Many<ServerSentEvent<KitchenOrderResponseDto>> sink = storeOrderSinks.remove(storeId);
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