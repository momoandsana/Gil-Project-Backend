package com.web.gilproject.service;

import com.web.gilproject.domain.Post;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    //기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; //1시간
    private final EmitterRepository emitterRepository;
    private final BoardRepository boardRepository;


    //클라이언트가 구독을 위해 호출하는 메소드
    @Override
    public SseEmitter subscribe(Long userId, String lastEventId) {
        log.info("클라이언트가 구독을 위해 호출하는 메소드");

        //데이터가 유실된 시점을 파악하여 유실된 데이터 전송을 다시 해주기 위함
        //만약 id값을 그대로 사용한다면 Last-Event-Id값이 의미 없어짐(유실된 데이터 찾을 방법 없음)
        //String emitterdId = userId + "_" + System.currentTimeMillis();

        //1. 현재 클라이언트를 위한 sseEmitter 객체 생성해서
        log.info("사용자 아이디를 기반으로 이벤트 Emiiter를 생성");
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);  //생성
        
        //2. DB에 저장
        emitterRepository.save(userId, emitter);               //저장
        log.info("Emitter 생성 완료! userId = {}, emitter={}", userId, emitter);

        // 3. 연결 (503 Service Unavailable 방지용 dummy event 전송
        // :sse연결이 이뤄진 후 하나의 데이터도 전송되지 않는다면 SseEmitter의 유효시간이 끝나면 503응답 발생
        sendToClient(userId, "EventStream Created. [userId = " + userId + "]");

        //4. 연결 종료 처리  (상황별 emitter 삭제 처리)
        //Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다
        emitter.onCompletion(()->emitterRepository.deleteById(userId));
        //Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(()->emitterRepository.deleteById(userId));
        //에러 발생시 Emitter를 삭제한다
        emitter.onError((e)->emitterRepository.deleteById(userId));

        return emitter;
        //////////////////////////////////////////
        //client가 미수신한 event 목록이 존재하는 경우
//        if(!lastEventId.isEmpty()){
//            Map<String, Object> eventCache = emitterRepository.getById(userId); // id에 해당하는 eventCache 조회
//            eventCache.entrySet().stream()
//                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
//                    .forEach(entry -> emitEvent);
//        }

    }

    //서버의 이벤트를 클라이언트에게 보내는 메소드
    // 다른 서비스 로직에서 이 메소드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
    @Override
    public void notify(Long userId, Object event) {
        log.info("서버의 이벤트를 클라이언트에게 보내는 메소드");
        sendToClient(userId, event);
        //emitterRepository.deleteById(userId); //이거 없으면 누적되는건가?(확인 필요)

        //try-catch로 잡아야함?

    }



    //클라이언트에게 데이터를 전송
    @Override
    public void sendToClient(Long userId, Object data) {
        log.info("클라이언트에게 데이터를 전송 userId={}, data={}", userId, data);
        //
        SseEmitter emitter = emitterRepository.getById(userId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(userId)).name("sse").data(data));
                log.info("댓글이 달렸다는 알림 전송");
            } catch (IOException e) {
                emitterRepository.deleteById(userId);
                emitter.completeWithError(e);
            }
        }


    }

    @Override
    public void notifyComment(Long postId) {
        Post post = boardRepository.findById(postId).orElseThrow( //userId찾기
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
        );

       Long userId = post.getId();

       if(emitterRepository.getById(userId) != null) {
           SseEmitter sseEmitter = emitterRepository.getById(userId);
           try {
               sseEmitter.send(SseEmitter.event().id(String.valueOf(userId)).name("addComment").data("댓글이 달렸습니다."));
           } catch (IOException e) {
               emitterRepository.deleteById(userId);
               sseEmitter.completeWithError(e);
           }


       }

    }


}
