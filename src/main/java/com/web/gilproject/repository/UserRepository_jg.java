package com.web.gilproject.repository;

import com.web.gilproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository_jg extends JpaRepository<User, Long> {

}
