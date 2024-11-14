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

    private Integer platform; // 0: 이메일, 1: 구글, 2: 네이버, 3: 카카오

    private String name;

    private String nickName;

    private String imageUrl;

    private String password;

    private String email;

    private Double latitude;

    private Double longitude;

    @CreationTimestamp
    private LocalDateTime joinDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    private Integer point;

    private Integer state; // 0: 정상 1: 탈퇴

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
