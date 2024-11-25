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

        String accessToken = jwtUtil.createJwt("access", customUserDetails, 1000 * 60 * 15L); //15분
        String refreshToken = jwtUtil.createJwt("refresh", customUserDetails, 1000 * 60 * 60 * 24 * 90L); //90일

        //프론트 : 쿠키로 들어온 토큰 -> localstorage에 저장
        response.addCookie(JWTUtil.createCookie("authorization", accessToken,false));
        //리프레시 토큰 쿠키로 저장
        response.addCookie(JWTUtil.createCookie("refresh", refreshToken,true));

        response.sendRedirect("http://localhost:3000/main");
    }

}
