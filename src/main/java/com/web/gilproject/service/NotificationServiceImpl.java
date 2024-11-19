package com.web.gilproject.service;

import com.web.gilproject.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    //기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;


    //클라이언트가 구독을 위해 호출하는 메소드
    @Override
    public SseEmitter subscribe(Long userId, String lastEventId) {
        SseEmitter emitter = createEmitter(userId);

        sendToClient(userId, "EventStream Created. [userId = " + userId + "]");
        return emitter;
    }

    //서버의 이벤트를 클라이언트에게 보내는 메소드
    // 다른 서비스 로직에서 이 메소드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
    @Override
    public void notify(Long userId, Object event) {
        sendToClient(userId, event);
    }

    //사용자 아이디를 기반으로 이벤트 Emiiter를 생성
    @Override
    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);

        //Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다
        emitter.onCompletion(()->emitterRepository.deleteById(userId));
        //Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(()->emitterRepository.deleteById(userId));

        return emitter;
    }

    //클라이언트에게 데이터를 전송
    @Override
    public void sendToClient(Long userId, Object data) {
        SseEmitter emitter = emitterRepository.getById(userId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(userId)).name("sse").data(data));
            } catch (IOException e) {
                emitterRepository.deleteById(userId);
                emitter.completeWithError(e);
            }
        }


    }

    ////////////////////////////////////////////////////////////////////
    //이벤트가 구독되어있는 클라이언트에게 데이터를 전송
    @Override
    public void broadcast(Long userId, String lastEventId) {

    }

}
