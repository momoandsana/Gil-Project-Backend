package com.web.gilproject.repository;

import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository_JHW extends JpaRepository<User, Long> {

    /**
     * 이메일로 유저 찾기
     * @param email
     * @return
     */
    Boolean existsByEmail(String email);
}
