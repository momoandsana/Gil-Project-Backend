package com.web.gilproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (!tmpDir.endsWith("/") && !tmpDir.endsWith("\\")) {
            tmpDir += "/";
        }
        String resourceLocation = "file:///" + tmpDir.replace("\\", "/");
        System.out.println("Serving /temp-images/** from " + resourceLocation);
        registry.addResourceHandler("/temp-images/**")
                .addResourceLocations(resourceLocation);
    }

}
