package ru.umagadzhi.like_service.kafka;

import org.springframework.stereotype.Component;
import ru.umagadzhi.like_service.dto.UserInfoResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserInfoResponseStorage {

    private final Map<Long, CompletableFuture<UserInfoResponse>> responseMap = new ConcurrentHashMap<>();

    // Создаём новый CompletableFuture для ожидающего ответа по postId
    public CompletableFuture<UserInfoResponse> createResponseFuture(Long postId) {
        CompletableFuture<UserInfoResponse> future = new CompletableFuture<>();
        responseMap.put(postId, future);

        // Логируем информацию о создании запроса
        System.out.println("Создан запрос для postId: " + postId + ", ожидается ответ.");
        return future;
    }

    // Завершаем CompletableFuture, когда получен ответ
    public void completeResponse(Long postId, UserInfoResponse response) {
        CompletableFuture<UserInfoResponse> future = responseMap.remove(postId);
        if (future != null) {
            future.complete(response);

            // Логируем информацию о завершении запроса
            System.out.println("Ответ получен для postId: " + postId + ", данные: " + response);
        } else {
            // Логируем, если не нашли ожидающий запрос
            System.out.println("Не найден запрос для postId: " + postId);
        }
    }
}
