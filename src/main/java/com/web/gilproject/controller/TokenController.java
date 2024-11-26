package com.web.gilproject.controller;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.jwt.JWTUtil;
import com.web.gilproject.repository.RefreshRepository;
import com.web.gilproject.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {
    private final ReissueService reissueService;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    /**
     * 리프레시 토큰 검증 엔드포인트
     * @param refreshToken
     * @return
     */
    @PostMapping("/verification")
    public ResponseEntity<String> handleVerifyRefresh(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = "refresh", required = false) String refreshToken ) {
        System.out.println("handleVerifyRefresh call");
        System.out.println("refreshToken: " + refreshToken);
        
        //리프레시 토큰 검증
        ResponseEntity<String> stringResponseEntity = reissueService.validateRefreshToken(refreshToken);
        if(stringResponseEntity !=null){
            System.out.println("검증을 실패한게 있음");
            return stringResponseEntity;
        }

        Long id = jwtUtil.getUserId(refreshToken);
        System.out.println("id: " + id);
        String nickName = jwtUtil.getUserNickname(refreshToken);
        System.out.println("nickName = " + nickName);

        CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id(id).nickName(nickName).build());

        //새로운 access 토큰 발급해주기
        String accessToken = jwtUtil.createJwt("access", customUserDetails, 1000 * 60 * 15L); //15분
        String newRefreshToken = jwtUtil.createJwt("refresh", customUserDetails, 1000 * 60 * 60 * 24 * 90L); //90일
        
        //DB 저장
        JWTUtil.addRefreshEntity(refreshRepository, id,newRefreshToken,1000 * 60 * 60 * 24 * 90L);

        response.setHeader("abc", "Bearer " + accessToken);
        response.addCookie(JWTUtil.createCookie("refresh", newRefreshToken));
        System.out.println("access 토큰이 헤더를 통해 발급, refresh 토큰은 쿠키를 통해 재발급되었습니다");

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
