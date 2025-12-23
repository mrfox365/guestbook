package com.example.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // Оскільки @EnableWebMvc вимикає автоконфігурацію статики,
    // ми мусимо вручну сказати Spring, де шукати index.html
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") // Усі запити
                .addResourceLocations("classpath:/static/"); // Шукати в папці resources/static
    }
}