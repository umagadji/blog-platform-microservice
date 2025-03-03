package ru.umagadzhi.post_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long authorId; // Ссылка на пользователя
    private CategoryResponse category;  // DTO для категории
}

