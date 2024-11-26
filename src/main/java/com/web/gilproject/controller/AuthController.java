package com.web.gilproject.controller;

import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.service.JoinService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JoinService joinService;

    /**
     * 회원가입
     * @param userDto
     * @return
     */
    @PostMapping("/join")
    public int joinProcess(UserDTO userDto) {
        int result = joinService.joinProcess(userDto);
        
        if(result ==0){
            System.out.println("이메일 회원가입 실패");

        }else{
            System.out.println("이메일 회원가입 성공");
        }

        return result;
    }

    /**
     * 닉네임 중복 확인
     */
    @GetMapping("/nickname/{nickname}")
    public int checkNickname(@PathVariable String nickname) {
        int result = joinService.checkDuplicateNickname(nickname);

        return result;
    }

    /**
     * 이메일 중복 확인
     * @param email
     * @return
     */
    @GetMapping("/email/{email}")
    public int checkEmail(@PathVariable String email) {
       int result = joinService.checkDuplicateEmail(email);

        return result;
    }
}
