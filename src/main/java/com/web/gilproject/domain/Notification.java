package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification { //알림

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id")
    @SequenceGenerator(name = "notification_id", sequenceName = "notification_id_seq")
    private Long id; //알림 식별번호

    private String type; //알림 타입 (COMMENT_NOTIFY | POST_NOTIFY)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user; //알림 받는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="POST_ID", nullable = false)
    private Post post; //게시글 번호(클릭해서 해당 링크로 이동 위함)

    private String content; //내용

    @CreationTimestamp
    private LocalDateTime date; //알림 발생 날짜

    private Integer state=0; //상태(읽음 확인) - 0:안읽음, 1:읽음

}
