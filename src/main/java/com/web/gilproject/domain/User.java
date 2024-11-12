package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.support.ReplaceOverride;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name="USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_id")
    @SequenceGenerator(name="user_id",sequenceName = "user_id_seq",allocationSize=1)
    private Long id;

    private Integer platform;

    private String name;

    private String nickName;

    private String imageUrl;

    private Integer state;

    @CreationTimestamp
    private LocalDateTime joinDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    // 어노테이션?
    private LocalDateTime leaveDate;

    private String latitude;

    private String longitude;

    private String email;

    private Integer point;

    @OneToMany(mappedBy ="user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Path> paths;

    @OneToMany(mappedBy="user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostLike> postLikes;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Subscribe> subscriptions;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Notification> notifications;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PostWishlist> postWishLists;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<ReplyLike> replyLikes;

}
