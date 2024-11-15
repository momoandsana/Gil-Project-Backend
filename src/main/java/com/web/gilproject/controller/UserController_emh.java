package com.web.gilproject.controller;

import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.service.UserService_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("user/")
@Slf4j
public class UserController_emh {

    private final UserService_emh userService;

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
     */
    @PutMapping("/mypage/update/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO){
        log.info("updateUser 메소드 call.... id = {}, userDTO = {}",id ,userDTO);
        userService.updateUser(id, userDTO);
        return "redirect:/user/mypage?id="+id;
    }

    /**
     * 내 주소 수정???
     */

    /**
     * 내 프로필 수정
     */


    /**
     * 내 경로 기록보기
     */


    /**
     * 내 게시물 보기
     */

}
