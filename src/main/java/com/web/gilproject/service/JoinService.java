package com.web.gilproject.service;

import com.web.gilproject.domain.UserEntity_JHW;
import com.web.gilproject.dto.JoinDTO;
import com.web.gilproject.repository.UserRepository_JHW;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {
    private final UserRepository_JHW userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository_JHW userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }

    public void joinProcess(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) return;
        UserEntity_JHW data = new UserEntity_JHW();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password)); //암호화
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
    }
}
