package com.web.gilproject.dto;


import com.web.gilproject.domain.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(of = {"id","name","nickName","imageUrl", "password", "email", "comment"})
public class UserDTO {

    private Long id;
    private Integer platform;
    private String name;
    private String nickName;
    private String imageUrl;
    private String password;
    private String email;
    private String comment;
    private Double latitude;
    private Double longitude;
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

    public UserDTO(User user) {
        this.id = user.getId();
        this.platform = user.getPlatform();
        this.name = user.getName();
        this.nickName = user.getNickName();
        this.imageUrl = user.getImageUrl();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.comment = user.getComment();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.joinDate = user.getJoinDate();
        this.updateDate = user.getUpdateDate();
        this.point = user.getPoint();
        this.state = user.getState();
    }
}
