package com.web.gilproject.repository;

import com.web.gilproject.domain.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PathRepository extends JpaRepository<Path, Long>{

    @Query("SELECT p " +
            "FROM Path p " +
            "JOIN p.user u " +
            "LEFT JOIN p.pins pin " +
            "WHERE u.id = :userId AND p.state = 1")
    List<Path> findPathByUserId(@Param("userId") Long userId);
}
