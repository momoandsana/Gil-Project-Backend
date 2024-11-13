package com.web.gilproject.jwt;

import com.web.gilproject.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 커스텀 로그인 필터
 */
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String userName = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println("userName = " + userName);

        //세번째 인자는 Role
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, password, null);
        System.out.println("authToken = " + authToken);
        return authenticationManager.authenticate(authToken);
    }


    //로그인 성공시 실행하는 메소드 (여기서 JWT 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        System.out.println("successful authentication");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String name = customUserDetails.getUsername();
        String email = customUserDetails.getEmail();

        String token = jwtUtil.createJwt(name,email,10 * 60 * 60 * 1000L); //10시간

        response.addHeader("Authorization", "Bearer " + token);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        System.out.println("unsuccessful authentication");

        response.setStatus(401);
    }
}
