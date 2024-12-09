package com.web.gilproject.controller;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.jwt.JWTUtil;
import com.web.gilproject.repository.RefreshRepository;
import com.web.gilproject.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class TokenController {
    private final ReissueService reissueService;
    private final JWTUtil jwtUtil;

    /**
     * 리프레시 토큰 검증 엔드포인트
     * @param refreshToken
     * @return
     */
    @PostMapping("/verification")
    public ResponseEntity<String> handleVerifyRefresh(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = "refresh", required = false) String refreshToken ) {
//        log.info("소셜로그인 refresh 토큰 검증 시작");

        ResponseEntity<String> stringResponseEntity = reissueService.validateRefreshToken(refreshToken);
        if(stringResponseEntity !=null){
//            log.info("refresh 토큰 검증 실패");
            return stringResponseEntity;
        }

        Long id = jwtUtil.getUserId(refreshToken);
        String nickName = jwtUtil.getUserNickname(refreshToken);
        CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id(id).nickName(nickName).build());

//        log.info("accessToken 생성");
        String accessToken = jwtUtil.createJwt("access", customUserDetails, 1000 * 60 * 60L); //1시간

//        log.info("access 토큰이 헤더를 통해 발급되었습니다");
        response.setHeader("oauth2access", "Bearer " + accessToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
