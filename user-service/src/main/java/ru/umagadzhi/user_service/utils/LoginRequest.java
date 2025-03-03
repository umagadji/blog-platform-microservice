package ru.umagadzhi.user_service.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//Класс описывает данные для авторизации пользователя
public class LoginRequest {
    private String username;
    private String password;
}
