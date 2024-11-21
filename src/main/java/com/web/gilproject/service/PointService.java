package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.repository.UserRepository_jg;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository_jg userRepository;

    public void pointPlus(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPoint(user.getPoint() + 10);
            userRepository.save(user); // 변경 사항 저장
        });
    }
}
