package com.web.gilproject.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmitterRepository {
    
    //모든 Emitters를 저장하는 ConcurrentHashMap (thread-safe 한 특징을 가지고 있음)
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();


    /**
     * 주어진 아이디와 이미터를 저장
     * @param id         - 사용자 아이디
     * @param emitter    - 이벤트 Emitter
     */
    public void save(Long id, SseEmitter emitter) {
        log.info("주어진 아이디와 이미터를 저장 id = {}, emitter = {}", id, emitter);
        emitters.put(id, emitter);
    }

    /**
     * 주어진 아이디의 Emitter를 제거
     * @param id        - 사용자 아이디
     */
    public void deleteById(Long id) {
        log.info("주어진 아이디와 이미터를 제거 id = {}", id);
        emitters.remove(id);
    }

    /**
     * 주어진 아이디의 Emitter를 가져옴
     * 
     * @param id    - 사용자 아이디
     * @return      - 이벤트 Emitter
     */
    public SseEmitter getById(Long id) {
        log.info("주어진 아이디의 Emitter를 가져옴 id={}", id);
        return emitters.get(id);
    }


}
