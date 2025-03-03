package ru.umagadzhi.like_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

//DTO объект для ответа на запрос из микросервиса like-service.
//Будет возвращаться этот ответ когда из like-service придет запрос с id поста и списком пользователей
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoResponse {
    private Long postId;
    private List<UserResponse> users;
}
