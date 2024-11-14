package com.web.gilproject.repository;

import com.web.gilproject.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Post,Long> {

}
