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
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("doFilterInternal Call");
        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        //request에서 Authorization 헤더를 찾음
        String accessToken = request.getHeader("authorization");

        //Authorization 헤더 검증
        if (accessToken == null) {

            System.out.println("token이 헤더에 없음");

            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("token 헤더에 있음!");

        //Bearer 부분 제거 후 순수 토큰만 획득
        accessToken = accessToken.split(" ")[1];

        //토큰 소멸 시간 검증
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            PrintWriter writer = response.getWriter();
            writer.println("token 기한만료");

//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
            response.setStatus(900);
            return;
        }
        System.out.println("token 만료되지 않았음!");

        //토큰이 access 토큰인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals("access")){
            PrintWriter writer = response.getWriter();
            writer.println("access 토큰이 아님");

//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
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
        //해당 사용자가 인증된 사용자라고 인식하도록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    /*private void doFilterInternalOAuth2(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("doFilterInternalOAuth2 Call");

        String authorization = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("authorization")) {
                authorization = cookie.getValue();
            }
        }

        //토큰이 있는지
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            filterChain.doFilter(request, response);

            return;
        }

        //토큰 만료됐는지
        if (jwtUtil.isExpired(authorization)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            return;
        }

        String username = jwtUtil.getUsername(authorization);
        UserDTO userDTO = new UserDTO();
        userDTO.setName(username);

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, null);
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }*/
}
