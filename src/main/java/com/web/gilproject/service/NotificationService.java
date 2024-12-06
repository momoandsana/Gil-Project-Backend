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
     * 클라이언트에게 데이터를 전송
     *
     * @param userId - 데이터를 받을 사용자의 아이디
     * @param data   - 전송할 데이터
     */
    void sendToClient(Long userId, String name, String comment, Object data);


    /**
     * 사용자의 알림 목록 조회
     * @param userId
     * @return
     */
    List<Notification> getNotificationsByUserId(Long userId);


    /**
     * 댓글 알림 - 게시글 작성자에게
     * @param replyId
     */
    void notifyComment(Long replyId);

    /**
     * 게시글 알림 - 게시글 작성한 유저를 구독한 사람들에게
     * @param postId
     */
    void notifyPost(Long postId);

    /**
     * 알림 읽음 처리
     * @param userId
     * @param notificationId
     */
    void markNotificationAsRead(Long userId, Long notificationId);

    /**
     * 알림 삭제
     * @param notificationId
     */
    void deleteNotification(Long userId, Long notificationId);

    /**
     * 알림 전체 삭제
     * @param userId
     */
    void deleteAllNotifications(Long userId);
}