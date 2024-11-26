package com.web.gilproject.service;

import com.web.gilproject.domain.WalkAlongs;
import com.web.gilproject.repository.PathRepository;
import com.web.gilproject.repository.UserRepository_jg;
import com.web.gilproject.repository.WalkAlongsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

                Double distance= path.getDistance();
            user.setPoint((int) (user.getPoint() + Math.round(distance / 10.0)));
            userRepository.save(user); // 변경 사항 저장


            WalkAlongs walkAlong = WalkAlongs
                    .builder()
                    .user(user)
                    .path(path)
                    .build();
            walkAlongsRepository.save(walkAlong);

            });
        });

    }

    public int getWalkAlongLength(Long userId){

        List<WalkAlongs> walkAlongs = walkAlongsRepository.findByUserId(userId);

        return walkAlongs.size();
    }
}
