package com.web.gilproject.controller;

import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.UserService_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("user/")
@Slf4j
public class UserController_emh {

    private final UserService_emh userService;
    private final AmazonService s3Service;

    /**
     * 내 정보 조회하기 (User정보, Path정보, Post정보, Subscribe정보, Wishlist정보)
     */
    @GetMapping("/mypage")
    public ResponseEntity<?> findUserById(Long id){
        log.info("findUserById 메소드 call..");
        UserDTO userDTO = userService.findUserById(id);
        log.info("userDTO = " + userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    /**
     * 내 정보 수정하기 (수정하기 or 뒤로가기 버튼 누르면)
     * ※구현 내용 : 닉네임, 자기소개글
     * ※확인 필요 : 닉네임, 비밀번호, 주소 보류(닉네임-중복 확인 필요, pw-암호화필요, 주소API연동)
     */
    @PutMapping("/mypage/update/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO){
        log.info("updateUser 메소드 call.... id = {}, userDTO = {}",id ,userDTO);
        userService.updateUserInfo(id, userDTO);
        return "redirect:/user/mypage?id="+id;
    }
    

    /**
     * 내 프로필 수정 (수정하고 기존꺼 삭제 필요??)
     */
    @PutMapping("/mypage/profile/{id}")
    public String updateUserProfile(@PathVariable Long id, @RequestParam("file") MultipartFile file){
        //업로드 하고 url받아서 db에 넣기
        //업로드
        try {
            String fileUrl = s3Service.uploadFile(file);
        } catch (IOException e) {
            return "프로필 이미지 파일 업로드 실패하였습니다.";
        }


        log.info("updateUserProfile : id={}", id);
        userService.updateUserImg(id);
        return "redirect:/user/mypage?id="+id;
    }


    /**
     * 내 경로 기록보기
     */


    /**
     * 내 게시물 보기
     */

}
