package com.web.gilproject.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * 토큰 내 이름 데이터 확인
     * @param token
     * @return
     */
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    /**
     * 토큰 내 이메일 데이터 확인
     * @param token
     * @return
     */
    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    /**
     * 토큰이 만료됐는지
     * @param token
     * @return
     */
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * JWT 생성
     * @param name 이름
     * @param email 이메일
     * @param expiredMs 만료시간(ms)
     * @return
     */
    public String createJwt(String name, String email, Long expiredMs) {

        return Jwts.builder()
                .claim("username",name)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis())) //생성일
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) //만료일
                .signWith(secretKey) //시그니처 부분
                .compact();
    }

}
