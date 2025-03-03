package ru.umagadzhi.like_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.umagadzhi.like_service.dto.UserInfoResponse;

@Service
public class UserInfoResponseConsumer {

    private final UserInfoResponseStorage responseStorage;

    public UserInfoResponseConsumer(UserInfoResponseStorage responseStorage) {
        this.responseStorage = responseStorage;
    }

    @KafkaListener(topics = "user-info-response", groupId = "like-service-group", containerFactory = "userInfoResponseKafkaListenerFactory")
    public void consumeUserInfoResponse(UserInfoResponse response) {
        System.out.println("Получены данные о пользователях: " + response);
        responseStorage.completeResponse(response.getPostId(), response); // Завершаем ожидающий CompletableFuture
    }

}
