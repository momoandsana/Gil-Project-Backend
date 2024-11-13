package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.UserDTO_JHW;
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

    public void joinProcess(UserDTO_JHW userDTOJHW) {
        //DTO 가져오기(이메일 회원가입 입력필드)
        String name = userDTOJHW.getName();
        String nickName =userDTOJHW.getNickName();
        String email = userDTOJHW.getEmail();
        String password = userDTOJHW.getPassword();

        Boolean isExist = userRepository.existsByEmail(email);

        if (isExist) return;

        //DTO -> Entity 데이터 주입
        User data = new User();
        data.setName(name);
        data.setNickName(nickName);
        data.setEmail(email);
        data.setPassword(bCryptPasswordEncoder.encode(password)); //암호화

        userRepository.save(data);
    }
}
