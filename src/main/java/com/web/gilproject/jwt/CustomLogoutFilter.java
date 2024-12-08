package com.web.gilproject.jwt;

import com.nimbusds.jwt.JWT;
import com.web.gilproject.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.cookie.SetCookie;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        log.info("로그아웃 필터 시작");
        
        //경로, 메소드 검사
//        log.info("경로, 메소드 검사 시작");
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
//            log.info("로그아웃 필터 끝");
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
//            log.info("로그아웃 필터 끝");
            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }

        //refresh null check
        if (refresh == null) {
            log.info("refresh 토큰이 쿠키에 없습니다, 400번 응답");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //만료 검사
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.info("refresh 토큰이 만료되었습니다, 400번 응답");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            log.info("refresh의 카테고리가 refresh가 아닙니다, 400번 응답");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            log.info("DB에 없는 refresh 토큰입니다, 400번 응답");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        refreshRepository.deleteByRefreshToken(refresh);
//        log.info("refresh 토큰 DB에서 제거");

//        log.info("refresh 토큰 쿠키에서 제거");
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

//        Cookie logincheckerCookie = new Cookie("loginchecker", null);
//        logincheckerCookie.setMaxAge(0);
//        logincheckerCookie.setPath("/");
//        response.addCookie(logincheckerCookie);

//        JWTUtil.removeCookie("refresh");
//        JWTUtil.removeCookie("loginchecker");


//        log.info("로그아웃 성공");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
