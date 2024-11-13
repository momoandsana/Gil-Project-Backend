package com.web.gilproject.repository;

import com.web.gilproject.domain.UserEntity_JHW;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository_JHW extends JpaRepository<UserEntity_JHW,Long> {

    /**
     * 유저이름으로 유저가 있는지 확인
     * @param username
     * @return
     */
    Boolean existsByUsername(String username);
}
