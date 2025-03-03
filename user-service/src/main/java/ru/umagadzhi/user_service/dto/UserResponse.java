package ru.umagadzhi.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//DTO для ответа при получении пользователя
public class UserResponse implements Serializable {
    private Long id;
    private String username;
    private String email;
}
