package com.web.gilproject.service;


import com.web.gilproject.domain.User;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.jwt.JWTUtil;
import com.web.gilproject.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("reissue Call");

        //get refresh token
        String refresh = null;

        //쿠키에서 refresh 토큰 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {  // 쿠키가 null이 아닌지 확인
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }

        ResponseEntity<String> stringResponseEntity = validateRefreshToken(refresh);
        if(stringResponseEntity != null) { //검증이 안되서 리턴된게 있다면
            return stringResponseEntity;
        }

        Long id = jwtUtil.getUserId(refresh);
        String nickName = jwtUtil.getUserNickname(refresh);

        CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id(id).nickName(nickName).build());

        //새로운 access 토큰 발급해주기
        String newAccessToken = jwtUtil.createJwt("access", customUserDetails, 1000 * 60 * 15L); //15분
        //새로운 refresh 토큰 발급해주기(refresh rotate)
        String newRefreshToken = jwtUtil.createJwt("refresh", customUserDetails, 1000 * 60 * 60 * 24 * 90L); //90일

        //기존 refresh DB에서 삭제
        refreshRepository.deleteByRefreshToken(refresh);
        //새 refresh DB에 넣기
        JWTUtil.addRefreshEntity(refreshRepository, id,newRefreshToken,1000 * 60 * 60 * 24 * 90L);

        response.setHeader("newaccess", "Bearer " + newAccessToken);
        response.addCookie(JWTUtil.createCookie("refresh", newRefreshToken));
        System.out.println("새로운 access 토큰이 헤더를 통해, refresh 토큰은 쿠키를 통해 재발급되었습니다");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 리프레시 토큰 검증
     * @return
     */
    public ResponseEntity<String> validateRefreshToken(String refresh){
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
            
            //DB에서 있으면 삭제
            if(refreshRepository.existsByRefreshToken(refresh)) {
                refreshRepository.deleteByRefreshToken(refresh);
            }
            
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

        // DB에 있는지 확인
        Boolean isExist = refreshRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            System.out.println("DB에 없는 refresh 토큰입니다");
            return new ResponseEntity<>("DB에 없는 refresh토큰입니다", HttpStatus.BAD_REQUEST);
        }

        return null;
    }


    /**
     * 기한만료된 토큰삭제 스케쥴러
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void cleanUpExpiredTokens() {
        System.out.println("기한만료 토큰 삭제 스케쥴러 실행");
        refreshRepository.deleteExpiredTokens();
    }

}
