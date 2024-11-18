package com.web.gilproject.repository;

import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository_JHW extends JpaRepository<User, Long> {

    /**
     * 이메일로 회원 조회
     *
     * @param email 회원 이메일
     * @return 회원 객체
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.platform = 0")
    User findByEmail(@Param("email") String email);

    /**
     * 이메일로 회원 찾기(소셜 가입자)
     *
     * @param email 회원 이메일
     * @return 회원 객체
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.platform != 0")
    User findUserWithPlatformNotZero(@Param("email") String email);

    /**
     * 닉네임으로 회원 조회
     */
    User findBynickName(String nickname);

}
