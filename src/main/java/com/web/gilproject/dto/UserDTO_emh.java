package com.web.gilproject.dto;


import com.web.gilproject.domain.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(of = {"id","name","nickName","password", "email", "comment"})
public class UserDTO_emh {

    private Long id;
    private Integer platform;
    private String name;
    private String nickName;
    private String imageUrl;
    private String password;
    private String email;
    private String comment;
    private String latitude;
    private String longitude;
    private LocalDateTime joinDate;
    private LocalDateTime updateDate;
    private Integer point;
    private Integer state;
    private Set<Post> posts;
    private Set<Path> paths;
    private Set<PostLike> postLikes;
    private Set<Subscribe> subscriptions;
    private Set<Notification> notifications;
    private Set<PostWishlist> postWishLists;
    private Set<ReplyLike> replyLikes;

    public UserDTO_emh(User user) {
        this.id = user.getId();
        this.platform = user.getPlatform();
        this.name = user.getName();
        this.nickName = user.getNickName();
        this.imageUrl = user.getImageUrl();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.joinDate = user.getJoinDate();
        this.updateDate = user.getUpdateDate();
        this.point = user.getPoint();
        this.state = user.getState();
    }
}
