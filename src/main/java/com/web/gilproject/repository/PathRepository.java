package com.web.gilproject.repository;

import com.web.gilproject.domain.Path;
import com.web.gilproject.dto.BoardDTO.BoardPathDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PathRepository extends JpaRepository<Path, Long> {
    @Query("SELECT new com.web.gilproject.dto.BoardDTO.BoardPathDTO(p.id, p.title, p.time, p.distance, p.startLat, p.startLong, p.route) " +
            "FROM Path p WHERE p.user.id = :userId")
    List<BoardPathDTO> findPathsByUserId(Long userId);

}
