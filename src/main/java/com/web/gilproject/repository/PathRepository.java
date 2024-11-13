package com.web.gilproject.repository;

import com.web.gilproject.domain.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PathRepository extends JpaRepository<Path, Long>{

    @Query("SELECT ST_AsText(p.route) FROM Path p")
    List<String> findAllRoutesAsWKT();
}
