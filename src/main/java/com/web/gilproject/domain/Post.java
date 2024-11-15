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
@DynamicUpdate //수정된 부분만 업데이트 되게하는 어노테이션
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

    private String title;

    @Column(columnDefinition = "TEXT") // 게시물 길이제한 x
    private String content;

    private String tag; //태그(후순위 활용)

    @CreationTimestamp
    private LocalDateTime writeDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    private Integer readNum; //조회수

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostLike> postLikes; //게시글 좋아요

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Reply> replies; //댓글

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostWishlist> postWishLists; //게시글 찜


}
