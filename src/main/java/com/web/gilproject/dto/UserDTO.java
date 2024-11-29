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

    private Long id;                   //회원번호
    private Integer platform;          //로그인 플랫폼
    private String name;               //회원 이름
    private String nickName;           //닉네임
    private String imageUrl;           //프로필 이미지
    private String password;           //비밀번호
    private String email;              //이메일
    private String comment;            //자기소개글
    private String address;            //집 주소
    private Double latitude;           //집 주소 위도
    private Double longitude;          //집 주소 경도
    private LocalDateTime joinDate;    //가입일자
    private Integer point;             //포인트
    private Integer postCount;         //내가 쓴 글 개수
    private Integer pathCount;         //따라걷기 개수
    private Integer subscribeByCount;  //구독자 수(나를 구독한 유저 수)

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
        this.point = user.getPoint();
    }
}
