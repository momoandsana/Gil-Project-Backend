package com.web.gilproject.service;


import com.web.gilproject.domain.*;
import com.web.gilproject.dto.NotificationResDTO;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.NotificationRepository;
import com.web.gilproject.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    //모든 Emitters를 저장하는 ConcurrentHashMap (thread-safe 한 특징을 가지고 있음) - 동시성을 고려
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final NotificationRepository notificationRepository;


    //클라이언트가 실시간 알림을 받기위해 호출하는 메소드 (구독이라고 표현함)
    @Override
    @Transactional
    public SseEmitter subscribe(Long userId) {
        //log.info("클라이언트가 구독을 위해 호출하는 메소드");

        //1. 현재 클라이언트를 위한 sseEmitter 객체 생성해서
        //log.info("사용자 아이디를 기반으로 이벤트 Emiiter를 생성");
        SseEmitter emitter = new SseEmitter(30*60*1000L); // 권장하는 30분으로 설정(기본은 30초임)
        
        //2. map에 사용자 ID로 Emitter 저장
        emitters.put(userId, emitter);
        //log.info("Emitter 생성 완료! userId = {}, emitter={}", userId, emitter);

        // 3. 초기 연결 메세지 전송 (503 응답 에러 방지용 초기 메세지 + 미확인 메세지 전송)
        // sse연결이 이뤄진 후 하나의 데이터도 전송되지 않고 SseEmitter의 유효시간이 끝나면 503응답(Service Unavailable) 발생

        //초기 연결 메세지 전송
        sendToClient(userId, "connection", "초기 연결 메세지","SSE 연결 성공");

        //내 알림 목록 전송
        List<Notification> myNotificationList = this.getNotificationsByUserId(userId);
        List<NotificationResDTO> notificationResDTOList = new ArrayList<>();

        myNotificationList.forEach(notification -> { //Entity -> DTO
            NotificationResDTO notificationResDTO = new NotificationResDTO(notification);
            notificationResDTOList.add(notificationResDTO);
        });
        sendToClient(userId, "myNotifications","내 알림 목록", notificationResDTOList);

        //4. 연결 종료 및 제거 리스너  (상황별 emitter 삭제 처리) -

        //Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다
        //이 메소드는 단순히 현재 연결의 리소스 정리하는 역할. 연결이 끊어지면 클라이언트 측에서 자동재연결 매커니즘 제공함
        emitter.onCompletion(()->{
            //log.info("Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태)");
            emitters.remove(userId);
        });
         //Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(()->{
            //log.info("server sent event timed out : id = {}", userId);
            emitters.remove(userId);
        });
         //에러 발생시 Emitter를 삭제한다
        emitter.onError((e)->{
            //log.info("server sent event error occured : id = {}, message={}", userId, e.getMessage());
            emitters.remove(userId);
        });

        return emitter;
    }


    // 서버의 이벤트를 클라이언트에게 보내는 메소드
    // 다른 서비스 로직에서 이 메소드를 사용해 이벤트를 전송하면 된다.
    @Override
    @Transactional
    public void sendToClient(Long userId, String name, String comment, Object data) {
        //log.info("클라이언트에게 데이터를 전송 userId={}, name={}, comment={}, data={}", userId, name, comment, data);

        //★이벤트 종류를 파악해서 보내는 데이터를 다르게하면 되겠다!
        SseEmitter emitter = emitters.get(userId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(name)
                        .comment(comment)
                        .data(data));

            } catch (IOException e) {
                //log.error("연결이 끊기거나 전송중 오류");
                emitters.remove(emitter);
                emitter.completeWithError(e);
            }
        }
    }

    //댓글 알림 - 게시글 작성자에게
    @Override
    @Transactional
    public void notifyComment(Long replyId) {
        //log.info("댓글 알림주러 가쟈~~ replyId = {}", replyId);

        //알림 보낼 댓글 정보
        Reply receivedReply = replyRepository.findById(replyId).orElse(null); //예외처리 필요

        //알림 받을 유저 찾기 (게시글 작성자)
        User user = receivedReply.getPost().getUser();
        Long userId = user.getId();

        //user:알림 받는 사람, type: 알림 타입, postId: 클릭시 링크위한 게시글 번호, content:댓글 내용
        Notification notification =
                Notification.builder()
                        .user(user)
                        .type("COMMENT_NOTIFY")
                        .post(receivedReply.getPost())
                        .content(receivedReply.getContent())
                        .state(0)
                        .build();

        //DB에 알림 내용 저장
        Notification dbNotification = notificationRepository.save(notification);

        //해당 유저가 알림 받을 준비가 되어있니? (=로그인이 되어있니?)
        if(emitters.containsKey(userId)) {

            //알림 보내기(실시간 알림)
            NotificationResDTO notificationResDTO = new NotificationResDTO(dbNotification); //Entity->DTO
            sendToClient(userId, "댓글 알림", "게시글에 댓글이 달렸어요!", notificationResDTO);
       }
    }


    //게시글 알림 - 게시글 작성자를 구독한 유저들에게
    @Override
    @Transactional
    public void notifyPost(Long postId) {
        //log.info("게시글 알림주러 가쟈~~ postId = {}", postId);
        
        //알림 보낼 게시글 정보
        Post post = boardRepository.findById(postId).orElse(null); //예외처리필요
        
        // 알림 받을 유저 찾기 (게시글 작성자를 구독한 유저들)
        Set<Subscribe> subscribes = post.getUser().getSubscribeBy(); //나를 구독한 유저 리스트

        subscribes.forEach(subscribe -> {
            User subscriber = subscribe.getUserId(); //알림 받을 유저 정보(=구독자)
            Long subscriberId = subscriber.getId();

            //user:알림 받는 사람, content:게시글 제목
            Notification notification =
                    Notification.builder()
                            .type("POST_NOTIFY")
                            .user(subscriber)
                            .post(post)
                            .content(post.getTitle())
                            .state(0)
                            .build();

            //DB에 알림 내용 저장
            Notification dbNotification = notificationRepository.save(notification);

            //해당 유저가 알림 받을 준비가 되어있니? (=로그인이 되어있니?)
            if(emitters.containsKey(subscriberId)) {

                //알림 보내기(실시간 알림)
                NotificationResDTO notificationResDTO = new NotificationResDTO(dbNotification); //Entity->DTO
                sendToClient(subscriberId,"게시글 알림","새 게시글이 등록되었어요!", notificationResDTO);
            }
        });
    }

    //사용자의 알림 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByDateDesc(userId);
    }

    //알림 읽음 처리
    @Override
    @Transactional
    public void markNotificationAsRead(Long userId, Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> notification.setState(1));
    }

    //알림 삭제
    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        notificationRepository.deleteByIdAndUserId(notificationId, userId);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }


}
