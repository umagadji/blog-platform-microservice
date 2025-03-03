package ru.umagadzhi.post_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//Класс описывающий запросы для CRUD операций категории
public class PostRequest {
    private Long id;
    @NotBlank(message = "Название статьи обязательно")
    private String title;
    @NotBlank(message = "Контент статьи обязателен")
    private String content;
    @NotNull(message = "Автор обязателен")
    private Long authorId;
    @NotNull(message = "Категория обязательна")
    private Long categoryId;
}
