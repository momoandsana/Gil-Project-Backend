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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl_emh implements UserService_emh{

    private final UserRepository_emh userRepository;
    private final PointService pointService;
    private final GilListRepository gilListRepository;
    private final SubscribeService subscribeService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AmazonService s3Service;

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
        Integer pathCount = pointService.getWalkAlongLength(userId);
        userDTO.setPathCount(pathCount);

        //날 구독한 유저 수
        Integer subscriberCount = subscribeService.findSubscribeByUserId(userId).size();
        userDTO.setSubscribeByCount(subscriberCount);

        return userDTO;
    }

    @Transactional
    @Override
    //내 정보 간단 조회 (프로필 이미지 눌렀을때 보이는 요약 버전)
    public UserSimpleResDTO findSimpleInfoById(Long userId, Long selectedUserId) {

        // user 정보 추출
        User userEntity = userRepository.findById(selectedUserId).orElse(null);
        UserSimpleResDTO userSimpleResDTO = new UserSimpleResDTO(userEntity);

        // 내가 쓴 글 개수 추출
        Long postCount = gilListRepository.countByUserId(selectedUserId);
        userSimpleResDTO.setPostCount(postCount.intValue());

        // 따라걷기한 경로 개수
        Integer pathCount = pointService.getWalkAlongLength(selectedUserId);
        userSimpleResDTO.setPathCount(pathCount);

        //날 구독한 유저 수
        List<Subscribe> subscribeList = subscribeService.findSubscribeByUserId(selectedUserId);
        Integer subscriberCount = subscribeList.size();
        userSimpleResDTO.setSubscribeByCount(subscriberCount);

        //조회한 유저가 나를 구독하고 있는지 여부
        subscribeList.forEach(subscribe -> {
            if(userId.equals(subscribe.getUserId().getId())){
                userSimpleResDTO.setIsSubscribed(1);
            } else {
                userSimpleResDTO.setIsSubscribed(0);
            }
        });
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
    public void updateUserImg(Long userId, MultipartFile file) {

        try {
            //s3에 있는 기존 파일 삭제
            User userEntity = userRepository.findById(userId).orElse(null);
            String preImageUrl = userEntity.getImageUrl();
            if(preImageUrl != null) {
                s3Service.deleteFile(preImageUrl);
                log.info("프로필 이전 이미지 삭제 완료!!");
            }

            //새로 입력된 이미지 s3에 업로드하고 fileUrl 받기
            String fileUrl = s3Service.uploadFileToFolder(file, "profile_images");

            //fileUrl DB에 저장
            userEntity.setImageUrl(fileUrl);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    //자기소개글 수정
    public void updateUserComment(Long userId, String newComment) {
        log.info("updateUserComment : newComment = {}, userDTO = {}" ,userId ,newComment);
        User userEntity = userRepository.findById(userId).orElse(null);
        userEntity.setComment(newComment);
    }

    @Transactional
    @Override
    //비밀번호 변경 (암호화된 비번 받아서 DB수정)
    public void updateUserPassword(Long userId, String password) {
        User userEntity = userRepository.findById(userId).orElse(null);
        String newPassword = bCryptPasswordEncoder.encode(password); //암호화
        userEntity.setPassword(newPassword);
    }

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

            // 따라걷기한 경로 개수
            Integer pathCount = pointService.getWalkAlongLength(userId);
            userSimpleResDTO.setPathCount(pathCount);

            //날 구독한 유저 수
            Integer subscriberCounts = subscribeService.findSubscribeByUserId(userId).size();
            userSimpleResDTO.setSubscribeByCount(subscriberCounts);

            userSimpleResDTOList.add(userSimpleResDTO);
        });
        return userSimpleResDTOList;
    }

    /**
     * 유저의 비밀번호와 일치하는지 비교
     * @param userId
     * @param password
     * @return
     */
    public Boolean matchUserPassword(Long userId, String password) {
        User userEntity = userRepository.findById(userId).orElse(null);
        return bCryptPasswordEncoder.matches(password, userEntity.getPassword());
    }

    @Transactional
    @Override
    public void updateUserNickname(Long id, String nickname) {
        User userEntity = userRepository.findById(id).orElse(null);
        userEntity.setNickName(nickname);
    }
}