package com.web.gilproject.controller;

import com.web.gilproject.dto.UserDTO;
import com.web.gilproject.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/join")
    public String joinProcess(UserDTO userDto) {
        int result = joinService.joinProcess(userDto);
        
        if(result ==0){
            System.out.println("이메일 회원가입 실패");
            //뷰에 실패 메세지 출력

        }else{
            System.out.println("이메일 회원가입 성공");
            //뷰에 성공 메세지 출력
        }

        //로그인 페이지로 리디렉션하게
        return "ok";
    }
}
