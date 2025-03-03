package ru.umagadzhi.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//DTO класс для запроса в микросервис user-service. Этот объект будем передавать через kafka в микросервис user-service
//Оборачиваем userIds и postId в UserInfoRequest.
public class UserInfoRequest {
    private List<Long> userIds;
    private Long postId;
}
