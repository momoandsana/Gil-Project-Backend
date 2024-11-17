package com.web.gilproject.dto.BoardDTO;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.User;

public record PostRequestDTO(Long pathId, String title, String content, String tag)
{
    public Post of(PostRequestDTO postRequestDTO, Long userId)
    {
        return Post.builder()
                .title(postRequestDTO.title())
                .content(postRequestDTO.content())
                .user(User.builder().id(userId).build())
                .tag(postRequestDTO.tag()).build();
    }
}
