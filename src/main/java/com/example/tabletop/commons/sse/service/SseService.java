package com.example.tabletop.commons.sse.service;

import com.example.tabletop.commons.sse.repository.SseRepository;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.service.MenuService;
import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.repository.OrderRepository;
import com.example.tabletop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

import static com.example.tabletop.commons.sse.repository.SseRepository.GET_EMITTER;
import static com.example.tabletop.commons.sse.repository.SseRepository.GET_EMITTER_MAP;

@Service
@RequiredArgsConstructor
public class SseService {
    private final OrderRepository orderRepository;

    public List<Order> subscribe(Long storeId) {
        SseEmitter emitter = createEmitter(storeId);
        GET_EMITTER_MAP().put(storeId, emitter);
        return orderRepository.findByStore_StoreId(storeId);
    }

    private SseEmitter createEmitter(Long storeId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitter.onCompletion(() -> GET_EMITTER_MAP().remove(storeId));
        emitter.onTimeout(() -> GET_EMITTER_MAP().remove(storeId));

        return emitter;
    }

    @Scheduled(fixedRate = 15000)
    public void sendKeepAlive() {
        GET_EMITTER_MAP().forEach((storeId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().comment("keepalive"));
            } catch (IOException e) {
                GET_EMITTER_MAP().remove(storeId);
            }
        });
    }

    public void notifyNewOrder(Long storeId, Order newOrder) {
        SseEmitter emitter = GET_EMITTER_MAP().get(storeId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-order")
                        .data(newOrder));
            } catch (IOException e) {
                GET_EMITTER_MAP().remove(storeId);
            }
        }
    }
}
