package com.web.gilproject.repository;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.WalkAlong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalkAlongRepository extends JpaRepository<WalkAlong, Long> {
    @Query("SELECT  w FROM WalkAlong w LEFT JOIN FETCH w.user WHERE w.user.id = :userId")
    List<WalkAlong> findByUserId();
}
