package com.web.gilproject.repository;

import com.web.gilproject.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //특정 사용자의 읽지 않은 알림 조회
    List<Notification> findByUserIdAndState(Long userId, Integer state);

    //특정 사용자의 알림 삭제
    void deleteByUserId(Long userId);


}
