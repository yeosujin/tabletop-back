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
@RequiredArgsConstructor
public class SseService {
  private final Map<Long, Sinks.Many<KitchenOrderResponseDto>> storeOrderSinks =
      new ConcurrentHashMap<>();

    public Flux<KitchenOrderResponseDto> getOrderStream(Long storeId) {
        return storeOrderSinks.computeIfAbsent(storeId, k -> Sinks.many().multicast().onBackpressureBuffer())
                .asFlux()
                .doOnNext(order -> log.info("주문 스트리밍: storeId={}, order={}", storeId, order))
                .doOnComplete(() ->log.info("SSE connection cancelled for store ID: {}", storeId));
    }

    public Mono<Void> notifyNewOrder(Long storeId, KitchenOrderResponseDto order) {
        log.info("notify new order: {}", order);
        return Mono.fromRunnable(() -> {
            storeOrderSinks.computeIfAbsent(storeId, k -> Sinks.many().multicast().onBackpressureBuffer())
                    .tryEmitNext(order);
        });
    }

    public void removeOrderStream(Long storeId) {
        storeOrderSinks.get(storeId).tryEmitComplete();
        storeOrderSinks.remove(storeId);
    }
}