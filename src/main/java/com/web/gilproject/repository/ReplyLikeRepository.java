package com.web.gilproject.repository;

import com.web.gilproject.domain.Reply;
import com.web.gilproject.domain.ReplyLike;
import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {
    Optional<ReplyLike> findByUserAndReply(User user, Reply reply);
}
