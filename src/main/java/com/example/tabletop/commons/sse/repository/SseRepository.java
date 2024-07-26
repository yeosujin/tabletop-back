package com.example.tabletop.commons.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseRepository {
    private static final Map<Long, SseEmitter> STRING_SSE_EMITTER_MAP = new ConcurrentHashMap<>();

    public static Map<Long, SseEmitter> GET_EMITTER_MAP() {
        return STRING_SSE_EMITTER_MAP;
    }

    public static SseEmitter GET_EMITTER(Long id) {
        return STRING_SSE_EMITTER_MAP.get(id);
    }

}
