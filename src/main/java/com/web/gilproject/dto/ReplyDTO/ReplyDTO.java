package com.web.gilproject.dto.ReplyDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.gilproject.domain.Reply;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReplyDTO(
    Long replyId,
    String content,
    String nickName,
    LocalDateTime replyDate,
    Long likesCount
) {
    public static ReplyDTO from(Reply reply) {
        return new ReplyDTO(
                reply.getId(),
                reply.getContent(),
                reply.getUser().getNickName(),
                reply.getWriteDate(),
                reply.getLikesCount()
        );
    }
}
