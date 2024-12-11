package com.web.gilproject.jwt;

import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 커스텀 로그인 필터
 */
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JWTUtil jwtUtil;
    private RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        setFilterProcessesUrl("/auth/login"); //진입경로 변경
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        log.info("커스텀 로그인 필터 call");
        String email = request.getParameter("email");  // form-data에서 email 값 추출
        String password = request.getParameter("password");  // form-data에서 password 값 추출

        //스프링 시큐리티의 자체 인증토큰
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        return authenticationManager.authenticate(authToken);
    }


    //로그인 성공시 실행하는 메소드 (여기서 JWT 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
//        log.info("로그인 성공 후 로직 시작");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

//        log.info("access토큰, refresh토큰 생성");
        String accessToken = jwtUtil.createJwt("access", customUserDetails, 1000 * 60 * 60 * 24L); // 24시간
        String refreshToken = jwtUtil.createJwt("refresh", customUserDetails, 1000 * 60 * 60 * 24 * 90L); //90일

        Boolean isExist = refreshRepository.existsByRefreshToken(refreshToken);
        if (!isExist)
        {
            log.info("생성된 refresh 토큰 DB에 생성");
            JWTUtil.addRefreshEntity(refreshRepository, customUserDetails.getId(), refreshToken, 1000 * 60 * 60 * 24 * 90L); //90일
        }

        response.setHeader("authorization", "Bearer " + accessToken);
        ResponseCookie refreshCookie = JWTUtil.createCookie("refresh", refreshToken);
        response.setHeader("Set-Cookie", refreshCookie.toString());

        response.setStatus(HttpStatus.OK.value());
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("로그인 실패");

        response.setStatus(401);
    }


}
