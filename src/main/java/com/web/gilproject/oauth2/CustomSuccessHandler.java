package com.web.gilproject.oauth2;

import com.web.gilproject.dto.CustomOAuth2User;
import com.web.gilproject.jwt.JWTUtil;
import com.web.gilproject.repository.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("소셜 로그인 성공");
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        Long id = customUserDetails.getId();

        log.info("refresh 토큰 생성");
        String refreshToken = jwtUtil.createJwt("refresh", customUserDetails, 1000 * 60 * 60 * 24 * 90L); //90일
        log.info("refresh 토큰 쿠키에 저장");
        response.addCookie(JWTUtil.createCookie("refresh", refreshToken));

        Boolean isExist = refreshRepository.existsByRefreshToken(refreshToken);
        if (!isExist) {
            log.info("refresh 토큰을 DB에 저장");
            JWTUtil.addRefreshEntity(refreshRepository, id, refreshToken, 1000 * 60 * 60 * 24 * 90L); //90일
        }

        response.sendRedirect("http://localhost:3000/main");
    }

}
