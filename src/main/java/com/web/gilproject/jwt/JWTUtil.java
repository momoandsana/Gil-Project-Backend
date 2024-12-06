package com.web.gilproject.jwt;

import com.web.gilproject.domain.Refresh;
import com.web.gilproject.dto.IntergrateUserDetails;
import com.web.gilproject.repository.RefreshRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * 토큰 내 이름 데이터 확인
     *
     * @param token
     * @return
     */
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    /**
     * 토큰 내 이메일 데이터 확인
     *
     * @param token
     * @return
     */
    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    /**
     * 토큰 내 id 데이터 확인
     *
     * @param token
     * @return
     */
    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    /**
     * 토큰 내 nickName 데이터 확인
     */
    public String getUserNickname(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);

    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    /**
     * 토큰이 만료됐는지
     *
     * @param token
     * @return
     */
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * JWT 생성
     *
     * @param category
     * @param userDetails 데이터를 감싸줄 유저객체
     * @param expiredMs   만료시간(ms)
     * @return
     */
    public String createJwt(String category, IntergrateUserDetails userDetails, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("nickname", userDetails.getNickname())
                .claim("id", userDetails.getId())
                .issuedAt(new Date(System.currentTimeMillis())) //생성일
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) //만료일
                .signWith(secretKey) //시그니처 부분
                .compact();
    }

    /**
     * 쿠키 생성
     *
     * @param key
     * @param value
     * @return
     */
    public static ResponseCookie createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .maxAge(60 * 60 * 24 * 90) // 90일
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("None")
                .domain("gilddara.vercel.app")
                .build();
    }

    /**
     * refresh 토큰을 DB에 저장
     *
     * @param userId
     * @param refresh
     * @param expiredMs
     */
    public static void addRefreshEntity(RefreshRepository refreshRepository, Long userId, String refresh, Long expiredMs) {

        LocalDateTime expirationTime = LocalDateTime.now().plus(expiredMs, ChronoUnit.MILLIS);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUserId(userId);
        refreshEntity.setRefreshToken(refresh);
        refreshEntity.setExpiration(expirationTime);

        refreshRepository.save(refreshEntity);
    }
}
