package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_like_id")
    @SequenceGenerator(name="post_like_id",sequenceName = "post_like_id",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="POST_ID",nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

}
