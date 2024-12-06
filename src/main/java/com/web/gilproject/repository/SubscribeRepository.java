package com.web.gilproject.repository;

import com.web.gilproject.domain.Subscribe;
import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    //나를 구독하고 있는 유저 목록 조회
    List<Subscribe> findAllBySubscribeUserId(User user);

    //내가 이미 구독하고 있는 유저가 아닌지 여부 확인
    Optional<Subscribe> findByUserIdAndSubscribeUserId(User user, User subUser);

    //내가 구독하고 있는 유저 목록 조회
    List<Subscribe> findByUserId(User user);


}
