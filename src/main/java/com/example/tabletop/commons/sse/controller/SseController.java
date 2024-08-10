package com.example.tabletop.commons.sse.controller;

import com.example.tabletop.commons.sse.service.SseService;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.order.dto.KitchenOrderResponseDto;
import com.example.tabletop.order.entity.Order;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
@Slf4j
public class SseController {
    private final SseService sseService;

    @GetMapping(value = "/orders/subscribe/{storeId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<KitchenOrderResponseDto>> streamOrders(@PathVariable Long storeId) {
        log.info("SSE connection attempt for store ID: {}", storeId);
        return sseService.getOrderStream(storeId)
                .doOnNext(sse -> log.info("Sending order to store ID: {}, Order: {}", storeId, sse.data()))
                .doOnComplete(() -> log.info("SSE stream completed for store ID: {}", storeId))
                .doOnError(error -> log.error("Error in SSE stream for store ID: {}", storeId, error));
    }

    @GetMapping("/orders/unsubscribe/{storeId}")
    public Mono<ResponseEntity<String>> unsubscribe(@PathVariable Long storeId) {
        return sseService.removeOrderStream(storeId)
                .then(Mono.just(ResponseEntity.ok("Unsubscribed successfully")))
                .doOnSuccess(result -> log.info("Unsubscribed store ID: {}", storeId))
                .onErrorResume(e -> {
                    log.error("Failed to unsubscribe store ID: {}", storeId, e);
                    return Mono.just(ResponseEntity.internalServerError().body("Failed to unsubscribe"));
                });
    }

    @PostMapping(value = "/notify/{storeId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")  // 명시적으로 모든 접근 허용
    public Mono<ResponseEntity<String>> notifyNewOrder(@PathVariable Long storeId, @RequestBody KitchenOrderResponseDto order) {
        log.info("Notifying new order for store ID: {}, Order: {}", storeId, order);
        return sseService.notifyNewOrder(storeId, order)
                .then(Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"message\": \"New order notified successfully\"}")))
                .onErrorResume(e -> {
                    log.error("Failed to notify new order for store ID: {}", storeId, e);
                    return Mono.just(ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body("{\"error\": \"Failed to notify new order: " + e.getMessage() + "\"}"));
                });
    }
}