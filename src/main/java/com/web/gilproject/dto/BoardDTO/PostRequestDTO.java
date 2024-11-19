package com.web.gilproject.dto.BoardDTO;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostRequestDTO(String title, String content, String tag, Long routeId, List<MultipartFile> images)
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
