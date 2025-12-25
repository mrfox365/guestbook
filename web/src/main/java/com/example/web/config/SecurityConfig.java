package com.example.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Статика та авторизація - доступні всім
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/register", "/login").permitAll()

                        // Перегляд книг - доступний USER і ADMIN (але не гостям)
                        .requestMatchers(HttpMethod.GET, "/books/**", "/users/**").hasAnyRole("USER", "ADMIN")

                        // Написання коментарів - доступний USER і ADMIN (але не гостям)
                        .requestMatchers(HttpMethod.POST, "/books/*/comments").hasAnyRole("USER", "ADMIN")

                        // Зміна даних (POST) - тільки ADMIN
                        .requestMatchers(HttpMethod.POST, "/books/**").hasRole("ADMIN")

                        // Всі інші запити вимагають авторизації
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/books", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/access-denied");
                        })
                );

        return http.build();
    }
}