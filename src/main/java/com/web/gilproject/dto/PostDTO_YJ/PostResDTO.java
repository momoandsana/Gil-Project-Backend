package com.web.gilproject.dto.PostDTO_YJ;


import com.web.gilproject.domain.Post;

import com.web.gilproject.dto.PathResDTO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResDTO {

    private Long postId; //게시글 id
    private String nickName; //게시글 작성자 정보
    private Long pathId; //경로 id
    private Double startLat; //시작위도
    private Double startLong; //시작경도
    private Integer state; //게시글 상태
    private String title; //게시글 제목
    private String content; //게시글 내용
    private String tag; //게시글 태그 - 후순위
    private LocalDateTime writeDate; //작성일
    private LocalDateTime updateDate; //수정일
    private Integer readNum; //조회수 - 후순위
    private Integer likesCount; //좋아요한 유저들의 숫자
    private Integer repliesCount; //댓글 갯수
    private Integer postWishListsNum; //찜한 사람들의 숫자

    private String userImgUrl;
    private PathResDTO pathResDTO;

    public PostResDTO(Post post) {
        this.postId = post.getId();
        this.nickName = post.getUser().getNickName();
        this.pathId = post.getPath().getId();
        this.startLat = post.getPath().getStartLat();
        this.startLong = post.getPath().getStartLong();
        this.state = post.getState();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.tag = post.getTag();
        this.writeDate = post.getWriteDate();
        this.updateDate = post.getUpdateDate();
        this.readNum = post.getReadNum();
        this.likesCount = post.getPostLikes().size();
        this.repliesCount = post.getReplies().size();
        this.postWishListsNum = post.getPostWishLists().size();

        this.userImgUrl = post.getUser().getImageUrl();
        this.pathResDTO = new PathResDTO();
    }
}
