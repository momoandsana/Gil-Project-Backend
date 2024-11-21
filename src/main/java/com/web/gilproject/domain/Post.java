package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//@Data
@Entity
@DynamicUpdate //수정된 부분만 업데이트 되게하는 어노테이션
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    private Integer state=0;

    private String title;

    @Column(columnDefinition = "TEXT") // 게시물 길이제한 x
    private String content;

    private String tag; //태그(후순위 활용)

    @CreationTimestamp
    private LocalDateTime writeDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @Column(nullable = false,columnDefinition = "int default 0")
    private Integer repliesCount=0;

    @Column(nullable=false,columnDefinition = "int default 0")
    private Integer likesCount=0;

    private Integer readNum=0; //조회수

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostLike> postLikes; //게시글 좋아요

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Reply> replies; //댓글

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostWishlist> postWishLists; //게시글 찜

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostImage> postImages;

    public void addPostImage(PostImage postImage) {
        if (this.postImages == null) {
            this.postImages = new HashSet<>();
        }
        this.postImages.add(postImage);
        postImage.setPost(this);
    }

    public void removePostImage(PostImage postImage) {
        if (this.postImages != null) {
            this.postImages.remove(postImage);
            postImage.setPost(null);
        }
    }

}
