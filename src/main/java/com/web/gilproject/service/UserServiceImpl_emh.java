package com.web.gilproject.service;


import com.web.gilproject.domain.User;
import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.repository.UserRepository_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl_emh implements UserService_emh{

    private final UserRepository_emh userRepository;

    @Transactional
    @Override
    public UserDTO findUserById(Long id) {
        log.info("findUserById : id = " +id);

        //id가 null이면 비회원으로 보여주기? or Error페이지?

        User dbUser = userRepository.findUserById(id);
        //log.info("dbUser = " + dbUser); //이거 풀면 쿼리를 모두 가져옴(비효율적)
        UserDTO userDTO = new UserDTO(dbUser); //Entity -> DTO변환
        log.info("userDTO = " + userDTO);
        return userDTO;
    }

    @Transactional
    @Override
    public void updateUser(Long id, UserDTO userDTO) {
        log.info("updateUser : id = {}, userDTO = {}" ,id ,userDTO);

        User userEntity = userRepository.findById(id).orElse(null); //★예외 처리 필요
        //수정(닉네임, 비밀번호, 주소, 자기소개글)
        userEntity.setNickName(userDTO.getNickName()); //★중복체크하는 메소드 활용 필요
        //userEntity.setPassword(userDTO.getPassword()); //★암호화 필요
        //★주소 변경 추가 필요(아직 컬럼 추가 안됨)
        userEntity.setComment(userDTO.getComment());
    }
}