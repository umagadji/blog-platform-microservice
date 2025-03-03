package ru.umagadzhi.like_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Класс описывающий ответы для CRUD операций лайков
public record LikeResponse(Long id, Long userId) {}
