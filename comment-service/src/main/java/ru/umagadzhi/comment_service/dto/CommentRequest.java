package ru.umagadzhi.comment_service.dto;

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
//Класс описывающий запросы для CRUD операций комментарий
public class CommentRequest {
    private Long id;
    @NotBlank(message = "Контент обязателен")
    private String content;
    @NotNull(message = "ID поста обязателен")
    private Long postId;
    @NotNull(message = "Автор обязателен")
    private Long authorId;
}
