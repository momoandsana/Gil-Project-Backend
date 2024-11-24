package com.web.gilproject.service;


import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;

    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        //get refresh token
        String refresh = null;

        //쿠키에서 refresh 토큰 찾기
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        //refresh 토큰 있는지 검사
        if (refresh == null) {
            System.out.println("쿠키에 refresh 토큰이 없습니다");
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST); //400
        }
//        System.out.println("쿠키에 refresh 토큰이 있습니다!");

        //만료된 토큰인지 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            System.out.println("refresh 토큰이 만료됐습니다");
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST); //400
        }
//        System.out.println("refresh 토큰이 만료되지않았습니다!");

        // 토큰이 refresh인지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            System.out.println("category가 refresh 토큰이 아닙니다");
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
//        System.out.println("category가 refresh가 맞습니다");

        Long id = jwtUtil.getUserId(refresh);
        String nickName = jwtUtil.getUserNickname(refresh);

        CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id(id).nickName(nickName).build());

        //새로운 access 토큰 발급해주기
        String newAccessToken = jwtUtil.createJwt("access", customUserDetails, 1000 * 60 * 15L); //15분
        //새로운 refresh 토큰 발급해주기
        String newRefreshToken = jwtUtil.createJwt("refresh", customUserDetails, 1000 * 60 * 60 * 24 * 90L); //90일

        response.setHeader("access", newAccessToken);
        System.out.println("새로운 access 토큰이 헤더를 통해 발급되었습니다");
        response.addCookie(JWTUtil.createCookie("refresh", newRefreshToken));
        System.out.println("새로운 refresh 토큰이 쿠키를 통해 발급되었습니다");

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
