package com.web.gilproject.dto.PostDTO_YJ;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.gilproject.domain.*;
import com.web.gilproject.dto.UserDTO;
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

    private String userImgURL;
    //private Path path;

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

        this.userImgURL = post.getUser().getImageUrl();
        //this.path = post.getPath();
    }

    public PostDTO(PostDTO postDTO) {
        this.id = postDTO.id;
        this.userNickName = postDTO.userNickName;
        this.pathId = postDTO.pathId;
        this.startLat = postDTO.startLat;
        this.startLong = postDTO.startLong;
        this.state = postDTO.state;
        this.title = postDTO.title;
        this.content = postDTO.content;
        this.tag = postDTO.tag;
        this.writeDate = postDTO.writeDate;
        this.updateDate = postDTO.updateDate;
        this.readNum = postDTO.readNum;
        this.postLikesUsers = postDTO.postLikesUsers;
        this.postLikesNum = postDTO.postLikesNum;
        this.repliesUsers = postDTO.repliesUsers;
        this.repliesNum = postDTO.repliesNum;
        this.postWishListsUsers = postDTO.postWishListsUsers;
        this.postWishListsNum = postDTO.postWishListsNum;

        this.userImgURL = postDTO.userImgURL;
        //this.path = postDTO.path;
    }
}
