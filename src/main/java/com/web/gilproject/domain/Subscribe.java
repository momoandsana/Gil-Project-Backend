package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class Subscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscribe_id")
    @SequenceGenerator(name="subscribe_id",sequenceName = "subscribe_id_seq",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    // 구독하는 회원 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUBSCRIBE_USER_ID",nullable = false)
    private User subscribeUser;

    @CreationTimestamp
    private LocalDateTime subscribeTime;

}
