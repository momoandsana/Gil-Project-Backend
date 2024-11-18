package com.web.gilproject.dto.BoardDTO;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.User;

public record PostRequestDTO(Long pathId, String title, String content, String tag)
{
    /*
    of 함수는 클라이언트로 받은 데이터를 기반으로 조립하거나, 다양한 값들을 사용해서
    조합하는 경우에 사용
     */
    public Post of(PostRequestDTO postRequestDTO, Long userId)
    {
        return Post.builder()
                .title(postRequestDTO.title())
                .content(postRequestDTO.content())
                .user(User.builder().id(userId).build())
                .tag(postRequestDTO.tag()).build();
    }
}
