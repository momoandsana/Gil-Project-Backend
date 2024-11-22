package com.web.gilproject.repository;

import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository_YJ extends JpaRepository<User, Long> {
    @Query("select u from User u where u.id=:userId")
    User findByUserId(@Param("userId") Long userId);
}
