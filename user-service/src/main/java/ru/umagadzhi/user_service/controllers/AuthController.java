package ru.umagadzhi.user_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.umagadzhi.user_service.entities.User;
import ru.umagadzhi.user_service.jwt.JwtUtil;
import ru.umagadzhi.user_service.services.UserService;
import ru.umagadzhi.user_service.utils.LoginRequest;

@RestController
@RequestMapping("/api/auth") //Все эндпоинты будут начинаться с "/api/auth"
public class AuthController {

    //Spring Security-компонент, который управляет процессом аутентификации.
    private final AuthenticationManager authenticationManager;
    //Сервис, который обрабатывает бизнес-логику, связанную с пользователями (поиск, регистрация и т. д.)
    private final UserService userService;
    //
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register") // Обрабатывает POST-запрос на "/api/auth/register"
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        //Проверяем, что поля не пустые
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Имя пользователя обязательно!");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("E-mail обязателен!");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Пароль обязателен!");
        }

        if (user.getRole() == null) {
            return ResponseEntity.badRequest().body("Роль должна быть указана!");
        }

        //Проверяем, существует ли уже пользователь с таким именем.
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует");
        }

        //Проверяем, существует ли пользователь с таким email
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Указанный e-mail уже используется");
        }

        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
        //return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
    }

    /*Как работает метод:
    * Запрос поступает в метод authenticateUser() контроллера AuthController
    * Создается объект UsernamePasswordAuthenticationToken, содержащий имя пользователя и пароль, переданные в запросе
    * Этот токен передается в authenticationManager.authenticate(), который проверяет правильность введенных данных
    * AuthenticationManager (или более конкретно DaoAuthenticationProvider) использует CustomUserDetailsService для загрузки пользователя из базы данных, с помощью метода loadUserByUsername()
    * Внутри метода loadUserByUsername() происходит запрос к базе данных через репозиторий пользователя (userRepository.findByUsername(username)).
    * Если пользователь с таким именем не найден, выбрасывается исключение UsernameNotFoundException
    * Если произошла ошибка аутентификации, например, неверное имя пользователя или пароль:
    *   Если ошибка связана с неверным паролем, будет выброшено исключение BadCredentialsException
    *   Если пользователя с таким именем не существует, будет выброшено исключение UsernameNotFoundException
    * Эти исключения перехватываются в глобальном обработчике исключений (GlobalExceptionHandler), который
    *   Для BadCredentialsException возвращает ответ с HTTP статусом 401 и сообщением "Неверное имя пользователя или пароль"
    *   Для UsernameNotFoundException возвращает ответ с HTTP статусом 401 и сообщением "Пользователь не найден"
    * Если данные правильные и аутентификация успешна, объект Authentication устанавливается в контекст безопасности Spring (SecurityContextHolder.getContext().setAuthentication(authentication)).
        Клиент получает успешный ответ с сообщением "Пользователь успешно аутентифицирован!"*/
    @PostMapping("/login") // Обрабатывает POST-запрос на "/api/auth/login"
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Аутентификация пользователя
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Установка аутентифицированного пользователя в контекст
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Генерация JWT-токена
            String jwtToken = jwtUtil.generateToken(loginRequest.getUsername());

            // Возвращаем токен в ответе
            return ResponseEntity.ok(jwtToken);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверное имя пользователя или пароль");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не найден");
        }
    }
}
