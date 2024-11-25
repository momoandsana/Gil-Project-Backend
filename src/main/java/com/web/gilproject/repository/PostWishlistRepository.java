package com.web.gilproject.repository;

import com.web.gilproject.domain.PostWishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostWishlistRepository extends JpaRepository<PostWishlist, Long> {
    PostWishlist findByUserIdAndPostId(Long userId, Long postId);
}
