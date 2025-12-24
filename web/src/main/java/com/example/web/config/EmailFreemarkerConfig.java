package com.example.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
public class EmailFreemarkerConfig {

    @Bean
    @Primary
    public freemarker.template.Configuration freemarkerEmailConfig() {
        try {
            freemarker.template.Configuration cfg =
                    new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);

            // Вказуємо шлях до папки з шаблонами листів
            cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "mail-templates");
            cfg.setDefaultEncoding("UTF-8");
            return cfg;
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure FreeMarker", e);
        }
    }
}