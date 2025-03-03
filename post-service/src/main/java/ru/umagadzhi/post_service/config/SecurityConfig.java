package ru.umagadzhi.post_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
                        .requestMatchers("/api/posts/**").permitAll()  // Разрешаем неавторизованный доступ к /api/posts/**
                        .requestMatchers("/api/categories/**").permitAll()  // Разрешаем неавторизованный доступ к /api/categories/**
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                );

        return http.build(); // Возвращаем настроенный SecurityFilterChain
    }

    /*Настройка CORS (Cross-Origin Resource Sharing).
    Разрешает запросы только с определённого фронтенд-домена.*/
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8082"));  // Разрешённые источники (фронтенд)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Разрешённые HTTP-методы
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Разрешённые заголовки
        configuration.setAllowCredentials(true); // Разрешаем передачу куки и заголовков авторизации
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Применяем CORS-правила для всех путей
        return source;
    }
}
