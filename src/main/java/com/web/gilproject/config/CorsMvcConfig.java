package com.web.gilproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**") //모든 경로에 대해서
                //.exposedHeaders("Set-Cookie")
//                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("https://gil-project-fe.vercel.app/")
//                .allowedOriginPatterns("https://gil-project-fe.vercel.app:*")

                .allowedMethods("*")
                .allowCredentials(true); //쿠키와 함께 요청 허용
    }
}
