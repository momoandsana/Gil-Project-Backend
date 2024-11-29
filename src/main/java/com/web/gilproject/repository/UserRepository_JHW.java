package com.web.gilproject.repository;

import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    /**
     * 이름, 이메일로 이메일 회원 찾기
     * @param name
     * @param email
     * @return
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.name= :name AND u.platform = 0 AND u.state=0")
    User findByNameAndEmail(@Param("name") String name, @Param("email") String email);

    /**
     * 유저 비밀번호 업데이트
     * @return
     */
    @Modifying
    @Query("UPDATE User u set u.password= :password where u.name= :name and u.email =:email AND u.platform=0 AND u.state=0")
    int updateUserPassword(@Param("name") String name, @Param("email") String email, @Param("password") String password);


}
