package com.web.gilproject.repository;

import com.web.gilproject.domain.Subscribe;
import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    Optional<Subscribe> findByUserIdAndSubscribeUserId(User user, User subUser);

    List<Subscribe> findByUserId(User user);
}
