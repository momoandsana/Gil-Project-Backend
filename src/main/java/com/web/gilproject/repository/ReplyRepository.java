package com.web.gilproject.repository;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.Reply;
import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPost(Post post);

    List<Reply> findByUser(User user);
}
