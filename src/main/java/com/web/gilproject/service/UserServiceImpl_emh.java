package com.web.gilproject.service;


import com.web.gilproject.domain.Subscribe;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.dto.UserSimpleResDTO;
import com.web.gilproject.repository.GilListRepository;
import com.web.gilproject.repository.UserRepository_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl_emh implements UserService_emh{

    private final UserRepository_emh userRepository;
    private final PathService pathService;
    private final GilListRepository gilListRepository;
    private final SubscribeService subscribeService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Transactional(readOnly = true)
    @Override
    //내 정보 조회
    public UserDTO findUserById(Long userId) {
        User userEntity = userRepository.findById(userId).orElse(null);
        //log.info("userEntity = " + userEntity); //이거 풀면 쿼리를 모두 가져옴(비효율적)
        UserDTO userDTO = new UserDTO(userEntity); //Entity -> DTO변환

        //내가 쓴 글 개수
        Long postCount = gilListRepository.countByUserId(userId);
        userDTO.setPostCount(postCount.intValue());

        //따라걷기한 경로 개수
        Integer pathCount = pathService.findPathByUserId(userId).size();
        userDTO.setPathCount(pathCount);

        //날 구독한 유저 수
        Integer subscriberCount = subscribeService.findSubscribeByUserId(userId).size();
        userDTO.setSubscribeByCount(subscriberCount);

        return userDTO;
    }

    @Transactional
    @Override
    //내 정보 간단 조회 (프로필 이미지 눌렀을때 보이는 요약 버전)
    public UserSimpleResDTO findSimpleInfoById(Long userId) {
        log.info("findSimpleInfoById : userId = " + userId);
        // user 정보 추출
        User userEntity = userRepository.findById(userId).orElse(null);
        UserSimpleResDTO userSimpleResDTO = new UserSimpleResDTO(userEntity);

        // 내가 쓴 글 개수 추출
        Long postCount = gilListRepository.countByUserId(userId);
        userSimpleResDTO.setPostCount(postCount.intValue());

        // 따라걷기한 경로 개수 (현재는 path, 나중에 따라걷기로 바꿔야함)
        Integer pathCounts = pathService.findPathByUserId(userId).size();
        userSimpleResDTO.setPathCount(pathCounts);

        //날 구독한 유저 수
        Integer subscriberCounts = subscribeService.findSubscribeByUserId(userId).size();
        userSimpleResDTO.setSubscribeByCount(subscriberCounts);
        log.info(userSimpleResDTO.toString());
        return userSimpleResDTO;
    }


    @Transactional
    @Override
    //내 주소 수정
    public void updateUserAddr(Long id, UserDTO userDTO) {
        log.info("updateUserAddr : id = {}, userDTO = {}" ,id ,userDTO);

        User userEntity = userRepository.findById(id).orElse(null);
        //주소 변경 ★API로 받아온 값 저장 필요
        if(userEntity != null){
            userEntity.setAddress(userDTO.getAddress());
            userEntity.setLongitude(userDTO.getLongitude());
            userEntity.setLatitude(userDTO.getLatitude());
        }

//         userRepository.updateUserAddress(id,userDTO.getAddress(),userDTO.getLongitude(),userDTO.getLatitude());
    }

    @Transactional
    @Override
    //내 프로필 이미지 수정
    public void updateUserImg(Long userId, String fileUrl) {
        User userEntity = userRepository.findById(userId).orElse(null);
        userEntity.setImageUrl(fileUrl);
    }

    @Transactional
    @Override
    //내 정보 수정
    public void updateUserInfo(Long userId, UserDTO userDTO) {
        log.info("updateUserInfo : userId = {}, userDTO = {}" ,userId ,userDTO);
        User userEntity = userRepository.findById(userId).orElse(null); //★예외 처리 필요

        //수정(닉네임, 이메일, 자기소개글)
        userEntity.setNickName(userDTO.getNickName());   //닉네임
        userEntity.setEmail(userDTO.getEmail());         //이메일(★검증필요)
        userEntity.setComment(userDTO.getComment());     //자기소개글
    }

    @Transactional
    @Override
    //비밀번호 변경 (암호화된 비번 받아서 DB수정)
    public void updateUserPassword(Long userId, String password) {
        User userEntity = userRepository.findById(userId).orElse(null);
        String newPassword = bCryptPasswordEncoder.encode(password); //암호화
        userEntity.setPassword(newPassword);
    }

//    @Transactional
//    @Override
//    //내 주소 수정
//    public void updateUserAddr(Long userId, UserDTO userDTO) {
//        log.info("updateUserAddr : userId = {}, userDTO = {}" ,userId ,userDTO);
//
//        User userEntity = userRepository.findById(userId).orElse(null);
//        //주소 변경 ★API로 받아온 값 저장 필요
//        userEntity.setAddress(userDTO.getAddress());
//        userEntity.setLatitude(userDTO.getLatitude());
//        userEntity.setLongitude(userDTO.getLongitude());
//    }

    //나를 구독한 유저 목록 조회
    @Transactional
    @Override
    public List<UserSimpleResDTO> findAllSubscribeByUserId(Long userId) {
        List<UserSimpleResDTO> userSimpleResDTOList = new ArrayList<>();
        List<Subscribe> mySubscribeList = subscribeService.findMySubscribeByUserId(userId);
        mySubscribeList.forEach(subscribe -> {
            UserSimpleResDTO userSimpleResDTO = new UserSimpleResDTO(subscribe.getSubscribeUserId());

            // 내가 쓴 글 개수 추출
            Long postCount = gilListRepository.countByUserId(userId);
            userSimpleResDTO.setPostCount(postCount.intValue());

            // 따라걷기한 경로 개수 (현재는 path, 나중에 따라걷기로 바꿔야함)
            Integer pathCounts = pathService.findPathByUserId(userId).size();
            userSimpleResDTO.setPathCount(pathCounts);

            //날 구독한 유저 수
            Integer subscriberCounts = subscribeService.findSubscribeByUserId(userId).size();
            userSimpleResDTO.setSubscribeByCount(subscriberCounts);

            userSimpleResDTOList.add(userSimpleResDTO);
        });
        return userSimpleResDTOList;
    }
}