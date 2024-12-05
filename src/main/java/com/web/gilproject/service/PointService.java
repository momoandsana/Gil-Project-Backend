package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.domain.WalkAlongs;
import com.web.gilproject.exception.ErrorCode;
import com.web.gilproject.exception.MemberAuthenticationException;
import com.web.gilproject.repository.PathRepository;
import com.web.gilproject.repository.UserRepository_jg;
import com.web.gilproject.repository.WalkAlongsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository_jg userRepository;
    private final WalkAlongsRepository walkAlongsRepository;
    private final PathRepository pathRepository;

    //따라걷기 끝났을 때 포인트+10,해당 아이디에 대한 따라걷기정보 DB에 저장.
    public void pointPlus(Long userId, Long pathId) {
        userRepository.findById(userId).ifPresent(user -> {
            pathRepository.findById(pathId).ifPresent(path -> {
                Double distance = path.getDistance();
                int additionalPoints;

                if (distance <= 0.1) {
                    additionalPoints = 10;
                } else {
                    additionalPoints = (int) Math.round(distance / 10.0);
                }

                user.setPoint(user.getPoint() + additionalPoints);
                userRepository.save(user);

                WalkAlongs walkAlong = WalkAlongs
                        .builder()
                        .user(user)
                        .path(path)
                        .build();
                walkAlongsRepository.save(walkAlong);
            });
        });
    }

    //로그인된 유저의 point 프론트에 뿌리기
    public int getPoint(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberAuthenticationException(ErrorCode.NOTFOUND_USER));
        return user.getPoint();
    }

    public int getWalkAlongLength(Long userId){

        List<WalkAlongs> walkAlongs = walkAlongsRepository.findByUserId(userId);

        return walkAlongs.size();
    }
}
