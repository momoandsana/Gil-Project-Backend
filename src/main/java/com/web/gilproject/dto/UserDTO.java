package com.web.gilproject.dto;


import com.web.gilproject.domain.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(of = {"id","name","nickName","imageUrl", "email", "comment","point","postCount","pathCount","subscribeByCount"})
public class UserDTO {

    private Long id;
    private Integer platform;
    private String name;
    private String nickName;
    private String imageUrl;
    private String password;
    private String email;
    private String comment;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime joinDate;
    private LocalDateTime updateDate;
    private Integer point;
    private Integer postCount; //내가 쓴 글
    private Integer pathCount; //따라걷기 개수
    private Integer subscribeByCount; //나를 구독한 유저 수

    public UserDTO(User user) {
        this.id = user.getId();
        this.platform = user.getPlatform();
        this.name = user.getName();
        this.nickName = user.getNickName();
        this.imageUrl = user.getImageUrl();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.comment = user.getComment();
        this.address = user.getAddress();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.joinDate = user.getJoinDate();
        this.updateDate = user.getUpdateDate();
        this.point = user.getPoint();
    }
}
