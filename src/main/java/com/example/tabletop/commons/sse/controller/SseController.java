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
    public Flux<ServerSentEvent<KitchenOrderResponseDto>> streamOrders(@PathVariable Long storeId, HttpServletResponse response) {
        log.info("SSE connection attempt for store ID: {}", storeId);
        response.addHeader("X-Accel-Buffering", "no");
        response.addHeader("Content-Type", "text/event-stream");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");
        log.info("Headers set for SSE connection, store ID: {}", storeId);
        return sseService.getOrderStream(storeId)
                .doOnNext(order -> log.info("Sending order to store ID: {}, Order: {}", storeId, order))
                .map(order -> ServerSentEvent.<KitchenOrderResponseDto>builder()
                        .data(order)
                        .build())
                .doOnComplete(() -> log.info("SSE stream completed for store ID: {}", storeId))
                .doOnError(error -> log.error("Error in SSE stream for store ID: {}", storeId, error));
    }


    @GetMapping(value = "/orders/unsubscribe/{storeId}")
    public void unsubscribe(@PathVariable Long storeId) {
        sseService.removeOrderStream(storeId);
    }

    @PostMapping("/notify/{storeId}")
    public Mono<ResponseEntity<String>> notifyNewOrder(@PathVariable Long storeId, @RequestBody KitchenOrderResponseDto order) {
        log.info("Notifying new order for store ID: {}", storeId);
        return sseService.notifyNewOrder(storeId, order)
                .map(result -> {
                    log.info("New order notified successfully for store ID: {}", storeId);
                    return ResponseEntity.ok("New order notified successfully");
                })
                .onErrorResume(e -> {
                    log.error("Failed to notify new order for store ID: {}", storeId, e);
                    return Mono.just(ResponseEntity.internalServerError().body("Failed to notify new order"));
                });
    }
}