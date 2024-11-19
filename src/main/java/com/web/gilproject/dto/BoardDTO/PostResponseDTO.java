package com.web.gilproject.dto.BoardDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.gilproject.domain.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL) // 객체를 직렬화할 때 null 인거는 제외
public record PostResponseDTO(Long postId, String nickName, String title, String content, String tag, BoardPathResponseDTO boardPathResponseDTO,
                              LocalDateTime createdAt, List<String> imageUrls)
{
    /*
    from 함수는 엔티티로부터 전송해야 하는 값들을 꺼낼 때 사용
    다양한 요소들을 조합하는 것이 아니라 엔티티에서
    클라이언트에 보내야 하는 부분들만 뽑아서 dto 를 만든다
    static 으로 작성하는 경우가 많음
     */
    public static PostResponseDTO from(Post postEntity)
    {
        return new PostResponseDTO(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.getUser().getNickName(),
                postEntity.getTag(),
                BoardPathResponseDTO.from(postEntity.getPath()),
                postEntity.getWriteDate(),
                postEntity.getPostImages()
                        .stream()
                        .map(image->image.getImageUrl())
                        .collect(Collectors.toList())
        );
    }
}
