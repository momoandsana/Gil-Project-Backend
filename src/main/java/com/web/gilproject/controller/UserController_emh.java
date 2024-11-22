package com.web.gilproject.controller;

import com.web.gilproject.dto.PathResDTO;
import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.dto.UserSimpleResDTO;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.PathService;
import com.web.gilproject.service.UserService_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController_emh {

    private final UserService_emh userService;
    private final AmazonService s3Service;
    private final PathService pathService;

    /**
     * 내 정보 조회하기 : 마이페이지 누르면 보이는 정보 모두 조회
     * (User정보, Path정보, Post정보, Subscribe정보, Wishlist정보)
     */
    @GetMapping("/mypage/{userId}")
    public ResponseEntity<?> findUserById(@PathVariable Long userId){
        log.info("findUserById 메소드 call..");
        UserDTO userDTO = userService.findUserById(userId);
        log.info("userDTO = " + userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * 내 정보 수정하기 (수정하기 or 뒤로가기 버튼 누르면)
     * ※구현 내용 : 닉네임, 이메일, 자기소개글
     * ※확인 필요 : 닉네임, 이메일, 비밀번호(닉네임-중복 확인 필요, 이메일-검증 필요, 비번-암호화필요)
     */
    @PutMapping("/mypage/update/{userId}")
    public String updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO){
        log.info("updateUser 메소드 call.... userId = {}, userDTO = {}",userId ,userDTO);
        userService.updateUserInfo(userId, userDTO);
        return "redirect:/user/mypage/"+userId;
    }
    

    /**
     * 내 프로필 수정 (s3에 올라와 있는 파일 삭제도 구현 필요 - 추후 진행 예정)
     */
    @PostMapping("/mypage/profile/{userId}")
    public String updateUserProfile(@PathVariable Long userId, @RequestParam("file") MultipartFile file){
        log.info("updateUserProfile : id={} file={} ", userId, file);
        try {
            //s3에 파일 업로드하고
            String fileUrl = s3Service.uploadFileToFolder(file,"profile_images");
            //업로드된 url받아서 DB에 저장(수정)
            userService.updateUserImg(userId, fileUrl);
            //s3에 있는 파일 삭제도 필요한가?
        } catch (IOException e) {
            return "파일 업로드 실패";
        }
        return "redirect:/user/mypage/"+userId;
    }

    /**
     * 내 주소 수정 (주소API로 집 주소, 집 위도, 집 경도 받아서 내용 수정)
     */
    @PutMapping("/mypage/address/{userId}")
    public String updateUserAddress(@PathVariable Long userId, @RequestBody UserDTO userDTO){
        log.info("updateUserAddress : id={} userDTO = {}", userId, userDTO);
        userService.updateUserInfo(userId, userDTO);
        return "redirect:/user/mypage/"+userId;
    }


    /**
     * 내 경로 기록보기
     */
    @GetMapping("/mypage/mypath/{userId}")
    public ResponseEntity<?> findPathById(@PathVariable Long userId){
        log.info("findPathById call..");
        List<PathResDTO> pathResDTOList = pathService.findPathByUserIdTransform(userId);
        return new ResponseEntity<>(pathResDTOList, HttpStatus.OK);
    }

    /**
     * 내 게시물 보기 (염진님이 구현해주실 예정)
     */


    /**
     * 프로필 눌렀을 때 다른 유저에게 보이는 내 정보 조회
     * (프로필 이미지, 닉네임, 자기소개글, 내가 쓴 글 개수, 구독자 수, 따라걷기 수)
     */
    @GetMapping("/simpleInfo/{userId}")
    public ResponseEntity<?> findSimpleInfoById(@PathVariable Long userId){
        log.info("userId={}", userId);
        UserSimpleResDTO userSimpleResDTO = userService.findSimpleInfoById(userId);
        return new ResponseEntity<>(userSimpleResDTO, HttpStatus.OK);
    }
}
