package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscribe_id")
    @SequenceGenerator(name="subscribe_id",sequenceName = "subscribe_id_seq",allocationSize = 1)
    private Long id; //구독 식별번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User userId; //회원 (구독하는 사람)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUBSCRIBE_USER_ID",nullable = false)
    private User subscribeUserId; // 구독받는 사람

    @CreationTimestamp
    private LocalDateTime subscribeDate; //구독 시작일

}
