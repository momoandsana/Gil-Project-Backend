package com.web.gilproject.repository;


import com.web.gilproject.domain.WalkAlongs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalkAlongsRepository extends JpaRepository<WalkAlongs, Long> {
    @Query("SELECT  w FROM WalkAlongs w LEFT JOIN FETCH w.user WHERE w.user.id = :userId")
    List<WalkAlongs> findByUserId(Long userId);
}
