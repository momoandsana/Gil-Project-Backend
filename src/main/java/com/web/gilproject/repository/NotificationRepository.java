package com.web.gilproject.repository;

import com.web.gilproject.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //특정 사용자의 알림 목록 조회
    List<Notification> findByUserIdOrderByDateDesc(Long userId);

    //특정 사용자의 선택한 알림 삭제
    void deleteByIdAndUserId(Long id, Long userId);

    //특정 사용자의 알림 전체 삭제
    void deleteByUserId(Long userId);
}
