package ru.umagadzhi.user_service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component  // Аннотация Spring, обозначающая, что этот класс является Spring-компонентом и будет автоматически управляться контейнером Spring.
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Наследуемся от OncePerRequestFilter, чтобы фильтр выполнялся один раз за запрос.

    private final JwtUtil jwtUtil; // Утилита для работы с JWT (генерация и валидация токенов).
    private final UserDetailsService userDetailsService; // Сервис для загрузки информации о пользователе.

    // Конструктор с внедрением зависимостей (JwtUtil и UserDetailsService).
    public JwtAuthenticationFilter(JwtUtil jwtUtil, @Qualifier("userService") UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Получаем заголовок Authorization из запроса.
        String authHeader = request.getHeader("Authorization");

        // Проверяем, что заголовок присутствует и начинается с "Bearer ".
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Передаём запрос дальше по цепочке фильтров.
            return;
        }

        // Извлекаем сам JWT-токен, удаляя префикс "Bearer ".
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token); // Извлекаем имя пользователя из токена.

        // Проверяем, что пользователь ещё не аутентифицирован в SecurityContext.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Загружаем пользователя из базы.

            // Проверяем валидность токена.
            if (jwtUtil.validateToken(token)) {
                // Создаём объект аутентификации с пользователем и его ролями.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Устанавливаем дополнительные детали запроса в объект аутентификации.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Устанавливаем объект аутентификации в SecurityContext.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Передаём запрос дальше по цепочке фильтров.
        filterChain.doFilter(request, response);
    }
}
