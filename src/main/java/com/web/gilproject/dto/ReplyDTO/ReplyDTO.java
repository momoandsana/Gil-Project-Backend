package com.web.gilproject.dto.ReplyDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.gilproject.domain.Reply;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReplyDTO(
    Long replyId,
    String content,
    String nickName,
    String userImageUrl,
    LocalDateTime replyDate,
    Long likesCount,
    Boolean isLiked
) {
    public static ReplyDTO from(Reply reply,Long userId) {
        boolean isLiked=reply.getReplyLikes()
                .stream()
                .anyMatch(like->like.getUser().getId().equals(userId));

        return new ReplyDTO(
                reply.getId(),
                reply.getContent(),
                reply.getUser().getNickName(),
                reply.getUser().getImageUrl(),
                reply.getWriteDate(),
                reply.getLikesCount(),
                isLiked
        );
    }
}
