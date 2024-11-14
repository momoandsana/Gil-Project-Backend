package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.UserDTO_JHW;
import com.web.gilproject.repository.UserRepository_JHW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final UserRepository_JHW userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(UserDTO_JHW userDTOJHW) {
        //DTO 가져오기(이메일 회원가입 입력필드)
        String name = userDTOJHW.getName();
        String nickName =userDTOJHW.getNickName();
        String email = userDTOJHW.getEmail();
        String password = userDTOJHW.getPassword();

        Boolean isExist = userRepository.existsByEmail(email);
        System.out.println("DB 내 중복이메일 존재");

        if (isExist) return;

        //DTO -> Entity 데이터 주입
        User data = new User();

        data.setPlatform(0);
        data.setName(name);
        data.setNickName(nickName);
        data.setPassword(bCryptPasswordEncoder.encode(password)); //암호화
        data.setEmail(email);
        data.setPoint(0);
        data.setState(0);
        data.setImageUrl("");
        data.setLongitude(0d);
        data.setLatitude(0d);

        userRepository.save(data);
    }
}
