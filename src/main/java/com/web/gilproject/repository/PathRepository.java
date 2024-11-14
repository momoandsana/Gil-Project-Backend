package com.web.gilproject.repository;

import com.web.gilproject.domain.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PathRepository extends JpaRepository<Path, Long> {
    List<Path> findByUserId(Long userId);

}
