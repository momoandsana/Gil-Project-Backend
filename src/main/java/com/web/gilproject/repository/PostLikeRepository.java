package com.web.gilproject.repository;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.PostLike;
import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
}
