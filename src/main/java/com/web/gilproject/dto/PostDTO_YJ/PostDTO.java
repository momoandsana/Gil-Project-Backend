package com.web.gilproject.dto.PostDTO_YJ;

import com.web.gilproject.domain.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id; //게시글 id
    private String userNickName; //게시글 작성자 정보
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
    private List<Long> postLikesUsers; //좋아요한 유저들의 id List
    private Integer postLikesNum; //좋아요한 유저들의 숫자
    private List<Long> repliesUsers; //댓글 단 유저들의 id List
    private Integer repliesNum; //댓글 갯수
    private List<Long> postWishListsUsers; //찜한 유저들의 id List
    private Integer postWishListsNum; //찜한 사람들의 숫자

    public PostDTO(Post post) {
        this.id = post.getId();
        this.userNickName = post.getUser().getNickName();
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
        this.postLikesUsers = post.getPostLikes().stream().map(PostLike -> PostLike.getUser().getId()).collect(Collectors.toList());
        this.postLikesNum = post.getPostLikes().size();
        this.repliesUsers = post.getReplies().stream().map(Reply -> Reply.getUser().getId()).collect(Collectors.toList());
        this.repliesNum = post.getReplies().size();
        this.postWishListsUsers = post.getPostWishLists().stream().map(PostWishlist -> PostWishlist.getUser().getId()).collect(Collectors.toList());
        this.postWishListsNum = post.getPostWishLists().size();
    }
}
