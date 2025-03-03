package ru.umagadzhi.like_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration //Конфигурационный класс
@EnableWebSecurity //Включаем Spring Security
public class SecurityConfig {

    /*Определение цепочки фильтров безопасности.
    Отключам CSRF, настраивам CORS, делам аутентификацию stateless,
    разрешам доступ к API авторизации и требум аутентификацию для остальных запросов.*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Отключаем защиту от CSRF (необходимость для REST API)
                .cors(Customizer.withDefaults()) // Включаем поддержку CORS с настройками по умолчанию
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Без сессий
                // Отключаем сессии, так как будем использовать JWT (аутентификация без сессий)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/likes/**").permitAll()  // Разрешаем неавторизованный доступ к /api/likes/**
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                );

        return http.build(); // Возвращаем настроенный SecurityFilterChain
    }

    /*Настройка CORS (Cross-Origin Resource Sharing).
    Разрешает запросы только с определённого фронтенд-домена.*/
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8084"));  // Разрешённые источники (фронтенд)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Разрешённые HTTP-методы
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Разрешённые заголовки
        configuration.setAllowCredentials(true); // Разрешаем передачу куки и заголовков авторизации
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Применяем CORS-правила для всех путей
        return source;
    }
}
