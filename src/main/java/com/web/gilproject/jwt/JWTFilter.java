package com.web.gilproject.jwt;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CustomOAuth2User;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT 검증 클래스
 */
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        log.info("JWT Filter access토큰 검증 시작");
        //응답 한글깨짐현상 해결
        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        //request에서 Authorization 헤더를 찾음
        String accessToken = request.getHeader("authorization");

        //Authorization 헤더 검증
        if (accessToken == null) {
            log.info("access token이 헤더에 없음");
            filterChain.doFilter(request, response);
            return;
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        accessToken = accessToken.split(" ")[1];

        //토큰 소멸 시간 검증
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            log.info("access토큰 만료");
            PrintWriter writer = response.getWriter();
            writer.println("access토큰 기한만료, 백에서 900번 응답");
            response.setStatus(900);
            return;
        }

        //토큰이 access 토큰인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals("access")){
            log.info("access토큰 카테고리 오류");
            PrintWriter writer = response.getWriter();
            writer.println("access 토큰이 아님");
            response.setStatus(900);
            return;
        }
        
        //토큰 검증 끝 -> 데이터를 가져와도 됨

        //userEntity를 생성하여 값 set
        User userEntity = new User();
        userEntity.setId(jwtUtil.getUserId(accessToken));
        userEntity.setNickName(jwtUtil.getUserNickname(accessToken));
        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, null);
        //스프링 시큐리티에 인증
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
