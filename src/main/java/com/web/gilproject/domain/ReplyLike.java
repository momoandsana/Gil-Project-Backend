package com.web.gilproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ReplyLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "reply_like_id")
    @SequenceGenerator(name="reply_like_id",sequenceName = "reply_like_id_seq",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPLY_ID",nullable = false)
    @JsonIgnore
    private Reply reply;
}
