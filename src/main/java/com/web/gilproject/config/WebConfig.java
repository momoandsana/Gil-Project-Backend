package com.web.gilproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
ToDo: 배포할 때 리눅스 경로에 맞게 수정하기
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String tmpDir = System.getProperty("java.io.tmpdir");

//        //리눅스
//        String resourceLocation = "file://" + tmpDir.replace("\\", "/");
//        System.out.println("Serving /temp-images/** from " + resourceLocation);
//        registry.addResourceHandler("/temp-images/**")
//                .addResourceLocations(resourceLocation);



        if (!tmpDir.endsWith("/") && !tmpDir.endsWith("\\")) {
            tmpDir += "/";
        }
        String resourceLocation = "file:///" + tmpDir.replace("\\", "/");
        System.out.println("Serving /temp-images/** from " + resourceLocation);
        registry.addResourceHandler("/temp-images/**")
                .addResourceLocations(resourceLocation);
    }

}
