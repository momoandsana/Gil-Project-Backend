package com.web.gilproject.service;

import com.web.gilproject.domain.Notification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {

    /**
     * 클라이언트가 구독을 위해 호출하는 메소드
     *
     * @param userId      - 구독하는 클라이언트의 사용자 아이디
     * @return SseEmitter   - 서버에서 보낸 이벤트 Emitter
     */
    SseEmitter subscribe(Long userId);

    /**
     * 서버의 이벤트를 클라이언트에게 보내는 메소드
     * 다른 서비스 로직에서 이 메소드를 사용해 데이터를 Object event에 넣고 전송하면 된다.
     *
     * @param userId - 메세지를 전송할 사용자의 아이디
     * @param event  - 전송할 이벤트 객체
     */
    void notify(Long userId, Object event);


    /**
     * 클라이언트에게 데이터를 전송
     *
     * @param userId - 데이터를 받을 사용자의 아이디
     * @param data   - 전송할 데이터
     */
    void sendToClient(Long userId, String name, Object data, String comment);

    /**
     * 댓글 알림 - 게시글 작성자에게
     * @param postId
     */
    void notifyComment(Long postId);

    /**
     * 사용자의 미확인 알림 조회
     * @param userId
     * @return
     */
    List<Notification> getUnreadNotifications(Long userId);

    /**
     * 알림 읽음 처리
     * @param userId
     * @param notificationId
     */
    void markNotificationAsRead(Long userId, Long notificationId);


}