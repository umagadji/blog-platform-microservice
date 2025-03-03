package ru.umagadzhi.user_service.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component  // Аннотация Spring, делает этот класс компонентом, который можно внедрять через @Autowired
public class JwtUtil {
    // Статический ключ для подписи JWT, сгенерированный через HMAC-SHA256.
    // Этот ключ должен быть секретным и использоваться для подписания и проверки JWT.
    private static final SecretKey SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Время жизни токена в миллисекундах (1 день)
    private final long expirationMs = 86400000; // Время жизни токена: 1 день (в миллисекундах)

    /**
     * Генерация JWT токена на основе имени пользователя.
     *
     * @param username имя пользователя, которое будет использовано в токене (subject).
     * @return сгенерированный JWT токен.
     */
    public String generateToken(String username) {
        // Создание JWT с использованием библиотеки JJWT
        return Jwts.builder()
                .setSubject(username)  // Устанавливаем имя пользователя как subject токена
                .setIssuedAt(new Date())  // Устанавливаем текущее время как время создания токена
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // Устанавливаем срок действия токена (1 день)
                .signWith(SECRET, SignatureAlgorithm.HS256) // Подписываем токен с использованием HMAC-SHA256 и секретного ключа
                .compact(); // Компонуем строку токена
    }

    //

    /**
     * Извлекает имя пользователя (subject) из переданного JWT токена.
     *
     * @param token JWT токен, из которого нужно извлечь имя пользователя.
     * @return имя пользователя, которое было в токене.
     */
    public String extractUsername(String token) {
        // Разбираем токен и извлекаем subject (имя пользователя)
        return Jwts.parserBuilder()
                .setSigningKey(SECRET) // Устанавливаем ключ для проверки подписи
                .build()
                .parseClaimsJws(token) // Разбираем JWT токен и проверяем подпись
                .getBody()
                .getSubject(); // Извлекаем subject (имя пользователя)
    }

    /**
     * Проверяет валидность JWT токена. Валидность включает проверку подписи
     * и срока действия токена.
     *
     * @param token JWT токен, который нужно проверить.
     * @return true, если токен валиден; false, если токен невалиден (например, подпись некорректна или токен просрочен).
     */
    public boolean validateToken(String token) {
        try {
            // Пытаемся разобрать токен и проверить его подпись
            Jwts.parserBuilder()
                    .setSigningKey(SECRET) // Устанавливаем ключ для проверки подписи
                    .build()
                    .parseClaimsJws(token); // Проверяем подпись и срок действия токена
            return true; // Если исключений не было, значит токен валиден
        } catch (JwtException e) { // Если токен недействителен (например, неверная подпись или токен просрочен)
            return false; // Возвращаем false, если токен невалиден
        }
    }
}
