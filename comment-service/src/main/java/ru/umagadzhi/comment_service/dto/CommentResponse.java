package ru.umagadzhi.comment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//Класс описывающий ответы для CRUD операций комментарий
public class CommentResponse {
    private Long id;
    private String content;
    private Long authorId;
}
