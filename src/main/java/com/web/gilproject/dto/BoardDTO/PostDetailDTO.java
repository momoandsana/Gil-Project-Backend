package com.web.gilproject.dto.BoardDTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.gilproject.domain.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public  record PostDetailDTO(
        Long id,
        String nickName,
        String title,
        String content,
        String tag,
        BoardPathResponseDTO boardPathResponseDTO,
        LocalDateTime createDate,
        List<String> imageUrls,
        Integer likesCount,
        boolean isLiked, // 해당 사용자가 좋아요 눌렀는지, 눌렀다면 1을 반호나
        boolean isWishListed, // 해당 사용자가 찜목록에 추가했는지
        Integer repliesCount

) {
    public static PostDetailDTO from(Post postEntity,Long userId)
    {
        return new PostDetailDTO(
                postEntity.getId(),
                postEntity.getUser().getNickName(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.getTag(),
                BoardPathResponseDTO.from(postEntity.getPath()),
                postEntity.getWriteDate(),

                postEntity.getPostImages()
                        .stream()
                        .map(image->image.getImageUrl())
                        .collect(Collectors.toList()),

                postEntity.getLikesCount(),

                postEntity.getPostLikes()
                                .stream()
                                        .anyMatch(like->like.getUser().getId().equals(userId)),

                postEntity.getPostWishLists()
                                .stream()
                                        .anyMatch(wishlist->wishlist.getUser().getId().equals(userId)),

                postEntity.getRepliesCount()
        );
    }

}
