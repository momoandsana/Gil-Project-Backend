package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PostWishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "post_wishlist_id")
    @SequenceGenerator(name="post_wishlist_id",sequenceName = "post_wishlist_id",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="POST_ID",nullable = false)
    private Post post;
}
