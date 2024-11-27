package com.web.gilproject.service;


import com.web.gilproject.domain.Notification;
import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.Reply;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.NotificationResDTO;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.NotificationRepository;
import com.web.gilproject.repository.ReplyRepository;
import com.web.gilproject.repository.UserRepository_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    //기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; //1시간

    //모든 Emitters를 저장하는 ConcurrentHashMap (thread-safe 한 특징을 가지고 있음) - 동시성을 고려
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository_emh userRepository;


    //클라이언트가 구독을 위해 호출하는 메소드
    @Override
    public SseEmitter subscribe(Long userId) {
        log.info("클라이언트가 구독을 위해 호출하는 메소드");

        //1. 현재 클라이언트를 위한 sseEmitter 객체 생성해서
        log.info("사용자 아이디를 기반으로 이벤트 Emiiter를 생성");
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);  //생성
        //60초 타임아웃
        //SseEmitter emitter = new SseEmitter(60_000L);
        
        //2. //map에 사용자 ID로 Emitter 저장
        emitters.put(userId, emitter);
        log.info("Emitter 생성 완료! userId = {}, emitter={}", userId, emitter);

        // 3. 초기 연결 메세지 전송 (503 Service Unavailable 방지용 dummy event 전송
        // :sse연결이 이뤄진 후 하나의 데이터도 전송되지 않는다면 SseEmitter의 유효시간이 끝나면 503응답 발생
        
        //미확인 알림 전송
        List<Notification> unreadNotificationList = this.getUnreadNotifications(userId);
        List<NotificationResDTO> notificationResDTOList = new ArrayList<>();

        unreadNotificationList.forEach(notification -> { //Entity -> DTO
            NotificationResDTO notificationResDTO = new NotificationResDTO(notification);
            notificationResDTOList.add(notificationResDTO);
        });
        sendToClient(userId, "unread_notifications", notificationResDTOList, "미 확인 알림 전송");
        
        //초기 연결 메세지 전송
        sendToClient(userId, "connection", "SSE 연결 성공", "초기 연결 메세지");

        //4. 연결 종료 및 제거 리스너  (상황별 emitter 삭제 처리)
         //Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다
//        emitter.onCompletion(()->{
//            log.info("Emitter가 완료될 때 (모든 데이터가 성공적으로 전송된 상태)");
//            emitters.remove(userId);
//        });
         //Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
//        emitter.onTimeout(()->{
//            log.info("server sent event timed out : id = {}", userId);
//            emitters.remove(userId);
//        });
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

    @Override
    public void notify(Long userId, Object event) {

    }


    //클라이언트에게 데이터를 전송
    // 서버의 이벤트를 클라이언트에게 보내는 메소드
    // 다른 서비스 로직에서 이 메소드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
    @Override
    public void sendToClient(Long userId, String name, Object data, String comment) {
        log.info("클라이언트에게 데이터를 전송 userId={}, name={}, comment={}, data={}", userId, name, comment, data);

        //★이벤트 종류를 파악해서 보내는 데이터를 다르게하면 되겠다!
        SseEmitter emitter = emitters.get(userId);
        if(emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(name)
                        .comment(comment)
                        .data(data));

            } catch (IOException e) {
                log.error("연결이 끊기거나 전송중 오류");
                emitters.remove(emitter);
                emitter.completeWithError(e);
            }
        }


    }

    //댓글 알림 - 게시글 작성자에게
    @Override
    public void notifyComment(Long postId) {
        log.info("댓글 알림주러 가쟈~~ postId = {}", postId);

        //게시글 작성자 찾기(알림 받을 유저)
        Post post = boardRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Long userId = post.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        log.info(" 게시글 작성자 찾았다! userId = {}", userId);

        //알림 보낼 댓글 정보
        Reply receivedReply = replyRepository.findFirstByPostIdOrderByWriteDateDesc(post.getId());
            if(receivedReply == null) {
                throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
            }
        log.info("알림 보낼 댓글 정보 찾았다! receivedReply = {}", receivedReply);

        Notification notification =
                Notification.builder().user(user).content(receivedReply.getContent()).state(0).build();

        //해당 유저가 알림 받을 준비가 되어있니?
        if(emitters.containsKey(userId)) {

            //DB에 알림 내용 저장
            Notification dbNotification = notificationRepository.save(notification);

            //알림 보내기
            NotificationResDTO notificationResDTO = new NotificationResDTO(dbNotification); //Entity->DTO
            sendToClient(userId, "CommentNotify", notificationResDTO, "게시글에 댓글이 달렸어요!");

       }

    }

    //사용자의 미확인 알림 조회
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndState(userId, 0);
    }
    
    //알림 읽음 처리
    @Override
    @Transactional
    public void markNotificationAsRead(Long userId, Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setState(1);
        });

    }

}
