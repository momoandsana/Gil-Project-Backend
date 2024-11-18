package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.repository.UserRepository_JHW;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final UserRepository_JHW userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public int joinProcess(UserDTO userDTO) {
        //DTO 가져오기(일반회원가입의 입력필드들)
        String name = userDTO.getName();
        String nickName =userDTO.getNickName();
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();

        //이메일 중복 확인
        if (userRepository.findByEmail(email) != null)
        {
            System.out.println("DB 내 중복이메일 존재");
            return 0;
        }
        //닉네임 중복 확인
        if(userRepository.findBynickName(nickName) != null){
            System.out.println("DB 내 중복 닉네임 존재");
            return 0;
        }

        //DTO -> Entity 데이터 주입
        User data = new User();

        data.setPlatform(0);
        data.setName(name);
        data.setNickName(nickName);
        data.setPassword(bCryptPasswordEncoder.encode(password)); //암호화
        data.setEmail(email);

        userRepository.save(data);
        return 1;
    }
}
