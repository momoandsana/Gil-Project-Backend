package com.web.gilproject.service;

import com.web.gilproject.domain.PostWishlist;

public interface PostWishlistService {

    /**
     *   찜한 게시글 찜 목록에 추가
     */
    int togglePostWishlist(Long userId, Long postId);

}
