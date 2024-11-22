package com.web.gilproject.repository;

import com.web.gilproject.domain.WalkAlong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkAlongRepository extends JpaRepository<WalkAlong, Long> {
}
