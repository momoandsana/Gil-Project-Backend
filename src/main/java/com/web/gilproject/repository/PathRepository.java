package com.web.gilproject.repository;

import com.web.gilproject.domain.Path;
import org.locationtech.jts.geom.LineString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PathRepository extends JpaRepository<Path, Long>{

    @Query("SELECT p FROM Path p LEFT JOIN FETCH p.pins WHERE p.user.id = :userId AND p.state = 0 ORDER BY p.createdDate DESC")
    List<Path> findPathByUserId(Long userId);

    @Query("SELECT p FROM Path p LEFT JOIN FETCH p.pins WHERE p.route = :route ORDER BY p.createdDate DESC")
    List<Path> findPathByRoute(@Param("route") LineString route);

     List<Path> findByUserId(Long userId);

    @Query("SELECT p FROM Path p LEFT JOIN FETCH p.pins WHERE p.state = 0 ORDER BY p.createdDate DESC")
     List<Path> findAllState();


}
