package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class Notification { //알림

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id")
    @SequenceGenerator(name = "notification_id", sequenceName = "notification_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

    private String content;

    @CreationTimestamp
    private LocalDateTime date; //알림 발생 날짜

    private Integer state; //읽음 확인
}
