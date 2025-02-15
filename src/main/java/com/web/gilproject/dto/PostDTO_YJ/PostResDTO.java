package com.web.gilproject.dto.PostDTO_YJ;


import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.PostImage;
import com.web.gilproject.dto.PathResDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResDTO {

    private Long postId; //게시글 id
    private Long postUserId;
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

    List<String> imageUrls;// 게시글 이미지들

    boolean isLiked;// 지금 로그인한 사용자가 해당 글을 좋아요 했는지
    boolean isWishListed;// "" 찜 목록에 추가했는지

    public PostResDTO(Post post,Long userId)
    {
        this.postId = post.getId();
        this.postUserId=post.getUser().getId();
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

        this.imageUrls = post.getPostImages()
                .stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        this.isLiked=post.getPostLikes()
                .stream()
                .anyMatch(like->like.getUser().getId().equals(userId));

        this.isWishListed=post.getPostWishLists()
                .stream()
                .anyMatch(postWishList->postWishList.getUser().getId().equals(userId));
    }
//
//    public PostResDTO(Optional<Post> post) {
//        this.postId = post.get().getId();
//        this.nickName = post.get().getUser().getNickName();
//        this.pathId = post.get().getPath().getId();
//        this.startLat = post.get().getPath().getStartLat();
//        this.startLong = post.get().getPath().getStartLong();
//        this.state = post.get().getState();
//        this.title = post.get().getTitle();
//        this.content = post.get().getContent();
//        this.tag = post.get().getTag();
//        this.writeDate = post.get().getWriteDate();
//        this.updateDate = post.get().getUpdateDate();
//        this.readNum = post.get().getReadNum();
//        this.likesCount = post.get().getPostLikes().size();
//        this.repliesCount = post.get().getReplies().size();
//        this.postWishListsNum = post.get().getPostWishLists().size();
//
//        this.userImgUrl = post.get().getUser().getImageUrl();
//        this.pathResDTO = new PathResDTO(post.get().getPath());
//
//        this.imageUrls = post.get().getPostImages()
//                .stream()
//                .map(PostImage::getImageUrl)
//                .collect(Collectors.toList());
//    }
}
