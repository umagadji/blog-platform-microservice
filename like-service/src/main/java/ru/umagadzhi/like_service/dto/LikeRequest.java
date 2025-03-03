package ru.umagadzhi.like_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Класс описывающий запросы для CRUD операций лайков
public record LikeRequest(Long postId, Long userId) {}
