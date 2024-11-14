package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.User;
import com.web.gilproject.repository.PathRepository;
import com.web.gilproject.repository.UserRepository_jg;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class PathService {

    private final PathRepository pathRepository;

    private final UserRepository_jg userRepository;

    //경로등록
    public Path insert(Path path) {
        Path savedPath= pathRepository.save(path);
        return savedPath;
    }

    //유저찾기 - 형우꺼 완성되면 형우꺼 쓸 수도 있음
    public User findById(User user) {
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + user.getId()));
    }


    public List<Path> findPathByUserId(Long userId) {
        return pathRepository.findPathByUserId(userId);
    }
}
