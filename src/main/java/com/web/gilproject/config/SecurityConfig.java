package com.web.gilproject.config;

import com.web.gilproject.jwt.CustomLogoutFilter;
import com.web.gilproject.jwt.JWTFilter;
import com.web.gilproject.jwt.JWTUtil;
import com.web.gilproject.jwt.LoginFilter;
import com.web.gilproject.oauth2.CustomSuccessHandler;
import com.web.gilproject.repository.RefreshRepository;
import com.web.gilproject.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    //OAuth
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * 비밀번호 해시 암호화
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //CORS 설정
        http
                .cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();

                        //허용할 주소
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//                        config.setAllowedOriginPatterns(Collections.singletonList("https://gil-project-fe.vercel.app:*"));
                        //허용할 메소드
                        config.setAllowedMethods(Collections.singletonList("*"));
                        //프론트에서 credential 설정을 할 경우 마찬가지로 true
                        config.setAllowCredentials(true);
                        //허용할 헤더
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);

                        //OAuth2
                        //config.setExposedHeaders(Collections.singletonList("Set-Cookie"));

                        //응답시 노출할 헤더
                        config.setExposedHeaders(Arrays.asList("authorization", "newaccess","oauth2access","Set-Cookie"));


                        return config;
                    }
                }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());
        //form 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler));

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/auth/**", "/mail/**", "/reissue","/notifications/**").permitAll() //로그인 안해도 요청 허용
                        .anyRequest().authenticated()); //나머지는 로그인한 사용자만 허용
//                        .anyRequest().permitAll()); //모든 요청 허용


        //세션 설정 - JWT에서는 항상 세션을 STATELESS 상태로 관리
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //커스텀 필터 등록
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);

        //로그아웃 필터 등록
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //JWT 필터 추가 - 통합
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
