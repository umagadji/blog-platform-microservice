package ru.umagadzhi.user_service.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice  // Аннотация, которая говорит Spring, что этот класс является обработчиком исключений для REST API.
// Все исключения, которые выбрасываются в контроллерах, будут перехвачены этим классом.
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)  // Этот метод обрабатывает исключения типа BadCredentialsException.
    // Когда в процессе аутентификации введены неверные данные (например, неправильный логин или пароль),
    // Spring вызовет этот метод.
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        // Возвращаем ответ с HTTP статусом UNAUTHORIZED (401) и сообщением "Неверное имя пользователя или пароль".
        // Это сообщение будет отправлено клиенту в теле ответа.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Неверное имя пользователя или пароль");
    }

    @ExceptionHandler(UsernameNotFoundException.class)  // Этот метод обрабатывает исключение UsernameNotFoundException.
    // Оно выбрасывается, если указанный логин не найден в базе данных.
    public ResponseEntity<String> handleUserNotFound(UsernameNotFoundException ex) {
        // Возвращаем ответ с HTTP статусом UNAUTHORIZED (401) и сообщением "Пользователь не найден".
        // Это сообщение будет отправлено клиенту в теле ответа.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Пользователь не найден");
    }

    @ExceptionHandler(IllegalArgumentException.class) // Этот метод обрабатывает исключение IllegalArgumentException.
    // Оно выбрасывается, если указанне данные не найдены. Будут использоваться при попытке найти например автора или категорию
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class) // Этот метод обрабатывает исключение IllegalStateException.
    //Будут использоваться при попытке лайкнуть уже лайкнутый пост
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    /**
     * Обрабатывает исключения, возникающие при валидации входных данных (@Valid).
     * Например, если поле @NotBlank в DTO окажется пустым.
     * @param ex исключение MethodArgumentNotValidException, выбрасываемое при невалидных данных
     * @return ResponseEntity с картой ошибок (поле -> сообщение об ошибке) и статусом 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Проходим по списку ошибок валидации и формируем ответ (ключ - имя поля, значение - сообщение об ошибке)
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        // Возвращаем JSON с ошибками и статусом 400
        return ResponseEntity.badRequest().body(errors);
    }
}
