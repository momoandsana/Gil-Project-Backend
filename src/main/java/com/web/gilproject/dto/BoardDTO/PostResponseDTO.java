package com.web.gilproject.dto.BoardDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.gilproject.domain.Post;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL) // 객체를 직렬화할 때 null 인거는 제외
public record PostResponseDTO(Long postId, String title, String content, String userName, String tag,
                              LocalDateTime regDate)
{
    public static PostResponseDTO from(Post postEntity)
    {
        return new PostResponseDTO(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.getUser().getName(),
                postEntity.getTag(),
                postEntity.getWriteDate()
        );
    }
}


//public static Post from(PostEntity postEntity)
//{
//    return new Post(
//            postEntity.getPostId(),
//            postEntity.getBody(),
//            postEntity.getRepliesCount(),
//            postEntity.getLikesCount(),
//            com.fastcampus.board.model.user.User.from(postEntity.getUser()),// 유저 레코드로 변환
//            postEntity.getCreatedDateTime(),
//            postEntity.getUpdatedDateTime(),
//            postEntity.getDeletedDateTime()
//    );