package com.web.gilproject.repository;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.dto.UserResDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository_jg extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.paths p WHERE p.id = :pathId")
    User findByPathId(@Param("pathId") Long pathId);
}
