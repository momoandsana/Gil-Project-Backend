package com.web.gilproject.repository;

import com.web.gilproject.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GilListRepository extends JpaRepository<Post, Long> {
    List<Post> findAll();
}
