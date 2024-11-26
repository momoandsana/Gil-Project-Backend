package com.web.gilproject.domain;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "USERS")
@DynamicInsert //회원가입 부분적 insert
@DynamicUpdate //수정된 부분만 업데이트 되게하는 어노테이션
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id")
    @SequenceGenerator(name = "user_id", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    private Integer platform; // 0: 이메일, 1: 구글, 2: 네이버, 3: 카카오

    private String name;

    private String nickName;

    private String imageUrl;

    private String password;

    private String email;

    private String comment; //자기소개글

    private String address; //집 주소
    
    private Double latitude;

    private Double longitude;

    @CreationTimestamp
    private LocalDateTime joinDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ColumnDefault("0")
    private Integer point;

    @ColumnDefault("0")
    private Integer state; // 0: 정상 1: 탈퇴

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Path> paths;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> postLikes;

    //내가 구독한 회원들(Subscribe 엔티티와 연결)
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true) //Subscribe 클래스의 userId 필드가 나를 참조
    private Set<Subscribe> subscriptions;

    // 나를 구독한 회원들(Subscribe 엔티티와 연결)
    @OneToMany(mappedBy = "subscribeUserId", cascade = CascadeType.ALL, orphanRemoval = true) //Subscribe 클래스의 subscribeUserId 필드가 나를 참조
    private Set<Subscribe> subscribeBy;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostWishlist> postWishLists;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReplyLike> replyLikes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WalkAlongs> walkAlongs;

}
