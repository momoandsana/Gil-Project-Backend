package com.web.gilproject.oauth2;

import com.web.gilproject.dto.CustomOAuth2User;
import com.web.gilproject.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("소셜 로그인 성공");        
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String token = jwtUtil.createJwt(customUserDetails, 1000 * 60 * 5L); //5분

        response.addCookie(createCookie("Authorization", token));
        //이 페이지에서 쿠키를 헤더에 담아 재요청을 보내는 axios 구현
        response.sendRedirect("http://localhost:3000/main");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(1000 * 60 * 5); //5분
        //cookie.setSecure(true); //https에서만 사용가능하도록
        cookie.setPath("/");
        cookie.setHttpOnly(true); //자바스크립트가 가져가지못하도록 설정

        return cookie;
    }
}
