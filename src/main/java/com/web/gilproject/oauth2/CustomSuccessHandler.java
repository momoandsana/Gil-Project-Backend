package com.web.gilproject.oauth2;

import com.web.gilproject.dto.CustomOAuth2User;
import com.web.gilproject.jwt.JWTUtil;
import com.web.gilproject.repository.RefreshRepository;
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
    private final RefreshRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("소셜 로그인 성공");
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        Long id = customUserDetails.getId();

//        String accessToken = jwtUtil.createJwt("access", customUserDetails, 1000 * 60 * 15L); //15분
        String refreshToken = jwtUtil.createJwt("refresh", customUserDetails, 1000 * 60 * 60 * 24 * 90L); //90일

//        response.addCookie(JWTUtil.createCookie("authorization", accessToken));
        response.addCookie(JWTUtil.createCookie("refresh", refreshToken));

        //DB 저장
        Boolean isExist = refreshRepository.existsByRefreshToken(refreshToken);
        if (!isExist) {
            JWTUtil.addRefreshEntity(refreshRepository, id, refreshToken, 1000 * 60 * 60 * 24 * 90L);
        }

        response.sendRedirect("http://localhost:3000/main");
    }

}
