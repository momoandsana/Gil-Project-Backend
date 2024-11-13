package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@DynamicUpdate
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "post_id")
    @SequenceGenerator(name="post_id",sequenceName = "post_id_seq",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable=false)
    private User user;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="PATH_ID",nullable=false)
    private Path path;

    private Integer state;

    @Column(columnDefinition = "TEXT") // 게시물 길이제한 x
    private String content;

    private String tag;

    @CreationTimestamp
    private LocalDateTime writeDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    // 어노테이션?
    private LocalDateTime deleteDate;

    private Integer readNum;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostLike> postLikes;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Reply> replies;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostWishlist> postWishLists;


}
