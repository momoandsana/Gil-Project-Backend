package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.User;
import com.web.gilproject.domain.WalkAlong;
import com.web.gilproject.dto.PointConfirmDTO;
import com.web.gilproject.repository.PathRepository;
import com.web.gilproject.repository.UserRepository_jg;
import com.web.gilproject.repository.WalkAlongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository_jg userRepository;
    private final WalkAlongRepository walkAlongRepository;
    private final PathRepository pathRepository;

    //따라걷기 끝났을 때 포인트+10,해당 아이디에 대한 따라걷기정보 DB에 저장.
    public void pointPlus(Long userId, Long pathId) {
        userRepository.findById(userId).ifPresent(user -> {
            pathRepository.findById(pathId).ifPresent(path -> {

                Double distance= path.getDistance();
            user.setPoint((int) (user.getPoint() + Math.round(distance / 10.0)));
            userRepository.save(user); // 변경 사항 저장


            WalkAlong walkAlong = WalkAlong
                    .builder()
                    .user(user)
                    .path(path)
                    .build();
            walkAlongRepository.save(walkAlong);

            });
        });

    }

    public int getWalkAlongLength(){
        List<WalkAlong> walkAlongs = walkAlongRepository.findByUserId();

        return walkAlongs.size();
    }
}
