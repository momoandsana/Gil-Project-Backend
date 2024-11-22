package com.web.gilproject.dto;

import com.web.gilproject.domain.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
public class UserSimpleResDTO {

    private Long id;                     //회원번호
    private String nickName;             //닉네임
    private String imageUrl;             //프로필 이미지
    private String comment;              //자기소개글
    private Integer postCounts;          //내가 쓴 글 개수
    private Integer pathCounts;          //따라걷기 개수
    private Integer subscriptionCounts;  //구독자 수(나를 구독하는 유저 수)

    public UserSimpleResDTO(User user) {
        this.id = user.getId();
        this.nickName = user.getNickName();
        this.imageUrl = user.getImageUrl();
        this.comment = user.getComment();
    }
}
