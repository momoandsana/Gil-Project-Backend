package com.web.gilproject.service;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.repository.UserRepository_JHW;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final UserRepository_JHW userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 이메일 회원가입
     * @param userDTO
     * @return
     */
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

    /**
     * 닉네임 중복 확인
     * @param nickName
     * @return 1:중복 0:중복아님
     */
    public int checkDuplicateNickname(String nickName) {
        User user =  userRepository.findBynickName(nickName);

        if(user !=null){
            return 1;
        }else{
            return 0;
        }
    }

    /**
     * 이메일 중복 확인
     * @param email
     * @return 1:중복 0:중복아님
     */
    public int checkDuplicateEmail(String email) {
        User user = userRepository.findByEmail(email);

        if(user !=null){
            return 1;
        }
        else{
            return 0;
        }
    }

    /**
     * 이름, 이메일로 유저 찾기
     * @param user
     * @return
     */
    public boolean existEmailUser(User user) {
        User u = userRepository.findByNameAndEmail(user.getName(),user.getEmail());
        return u != null;
    }

    @Transactional
    public int updateUserPassword(User user, String newPassword) {
        return userRepository.updateUserPassword(user.getName(),user.getEmail(),newPassword);
    }

}
