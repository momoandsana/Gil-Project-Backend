package com.web.gilproject.repository;

import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository_JHW extends JpaRepository<User, Long> {

    /**
     * 이메일로 회원 찾기
     * @param email 회원 이메일
     * @return 회원 유무
     */
    Boolean existsByEmail(String email);

   /**
     * 이메일을 받아 DB에 있는 회원조회
     * @param email 회원 이메일
     * @return 회원 객체
     */
    User findByEmail(String email);
}
