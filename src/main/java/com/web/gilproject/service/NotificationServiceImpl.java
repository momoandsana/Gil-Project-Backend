package com.web.gilproject.service;

import com.web.gilproject.domain.Notification;
import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.Reply;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.NotificationRepository;
import com.web.gilproject.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    //기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; //1시간

    //모든 Emitters를 저장하는 ConcurrentHashMap (thread-safe 한 특징을 가지고 있음)
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final NotificationRepository notificationRepository;
    private static Map<Long, Integer> notificationCounts = new HashMap<>(); // 알림 개수 저장

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
        
        //2. map<Long, SseEmitter> 에 저장
        log.info("주어진 아이디와 이미터를 저장 id = {}, emitter = {}", userId, emitter);
        emitters.put(userId, emitter); //저장
        log.info("Emitter 생성 완료! userId = {}, emitter={}", userId, emitter);

        // 3. 연결 (503 Service Unavailable 방지용 dummy event 전송
        // :sse연결이 이뤄진 후 하나의 데이터도 전송되지 않는다면 SseEmitter의 유효시간이 끝나면 503응답 발생
        sendToClient(userId, "EventStream Created. [userId = " + userId + "]");

        //4. 연결 종료 처리  (상황별 emitter 삭제 처리)
         //Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다
        emitter.onCompletion(()->{
            log.info("Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태)");
            emitters.remove(userId);
        });
         //Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(()->{
            log.info("server sent event timed out : id = {}", userId);
            emitters.remove(userId);
        });
         //에러 발생시 Emitter를 삭제한다
        emitter.onError((e)->{
            log.info("server sent event error occured : id = {}, message={}", userId, e.getMessage());
            emitters.remove(userId);
        });

        return emitter;

        //////////////////////////////////////////
        //client가 미수신한 event 목록이 존재하는 경우
//        if(!lastEventId.isEmpty()){
//            Map<String, Object> eventCache = emitters.get(userId); // id에 해당하는 eventCache 조회
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


    }



    //클라이언트에게 데이터를 전송
    @Override
    public void sendToClient(Long userId, Object data) {
        log.info("클라이언트에게 데이터를 전송 userId={}, data={}", userId, data);
        //
        SseEmitter emitter = emitters.get(userId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(userId)).name("sse").data(data));
                log.info("알림 전송");
            } catch (IOException e) {
                emitters.remove(emitter);
                emitter.completeWithError(e);
            }
        }


    }

    //댓글 알림 - 게시글 작성자에게
    @Override
    public void notifyComment(Long postId) {

        log.info("댓글 알려주러 가쟈~~ postId = {}", postId);
        Post post = boardRepository.findById(postId).orElseThrow( //userId찾기
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Long userId = post.getUser().getId();
        log.info(" 게시글 작성자 찾았다! userId = {}", userId);

        Reply receivedReply = replyRepository.findById(post.getId()).orElseThrow(
                ()->new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        log.info(" 댓글 작성자 찾았다! receivedReply = {}", receivedReply);

       if(emitters.containsKey(userId)) {
           log.info("여기 들어왔니...?");
           SseEmitter sseEmitter = emitters.get(userId);
           try {
               Map<String, String> eventData = new HashMap<>();
               eventData.put("message", "댓글이 달렸습니다.");
               eventData.put("sender", receivedReply.getUser().getNickName());
               eventData.put("content", receivedReply.getContent());

               sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

               //DB 저장
               Notification notification = new Notification();
               notification.setUser(post.getUser());
               notification.setContent(receivedReply.getContent());
               notificationRepository.save(notification); //DB에 저장
               log.info("DB저장 완료");

               //알림 개수 증가
               notificationCounts.put(userId, notificationCounts.getOrDefault(userId, 0) + 1);

               //현재 알림 개수 전송
               sseEmitter.send(SseEmitter.event().name("notificationCounts").data(notificationCounts.get(userId)));

           } catch (IOException e) {
               emitters.remove(userId);
               sseEmitter.completeWithError(e);
           }


       }

    }


}
