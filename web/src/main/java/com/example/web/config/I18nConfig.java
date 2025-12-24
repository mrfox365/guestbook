package com.example.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.Duration;
import java.util.Locale;

@Configuration
public class I18nConfig implements WebMvcConfigurer {

    // 1. Зберігаємо вибір мови в Cookies (щоб пам'ятав навіть після перезавантаження)
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("lang_cookie");
        resolver.setDefaultLocale(Locale.forLanguageTag("uk")); // Типова мова - UA
        resolver.setCookieMaxAge(Duration.ofDays(7));
        return resolver;
    }

    // 2. Слідкуємо за параметром "?lang=" в URL
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // Шукаємо саме слово "lang" в адресі
        return lci;
    }

    // 3. Реєструємо цей слідкувач у Spring
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}