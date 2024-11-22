package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.domain.WalkAlong;
import com.web.gilproject.dto.PointConfirmDTO;
import com.web.gilproject.repository.UserRepository_jg;
import com.web.gilproject.repository.WalkAlongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository_jg userRepository;
    private final WalkAlongRepository walkAlongRepository;

    public void pointPlus(Long userId, PointConfirmDTO pointConfirmDTO) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPoint(user.getPoint() + 10);
            userRepository.save(user); // 변경 사항 저장
            WalkAlong walkAlong = WalkAlong
                    .builder()
                    .user(user)
                    .distance(pointConfirmDTO.getDistance())
                    .time(pointConfirmDTO.getTime())
                    .build();
            walkAlongRepository.save(walkAlong);
        });
    }
}
