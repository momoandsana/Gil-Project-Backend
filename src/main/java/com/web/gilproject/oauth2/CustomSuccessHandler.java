package com.web.gilproject.oauth2;

import com.web.gilproject.dto.CustomOAuth2User;
import com.web.gilproject.jwt.JWTUtil;
import jakarta.servlet.ServletException;
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

        String token = jwtUtil.createJwt("access", customUserDetails, 1000 * 60 * 15L); //15분

        response.addCookie(JWTUtil.createCookie("authorization", token));

        ////////////URL 파라미터로 전달하기로 변경하기

        //이 페이지에서 쿠키를 헤더에 담아 재요청을 보내는 axios 구현
        response.sendRedirect("http://localhost:3000/main");
    }

}
