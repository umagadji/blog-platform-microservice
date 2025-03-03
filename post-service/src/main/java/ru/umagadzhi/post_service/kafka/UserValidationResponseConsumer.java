package ru.umagadzhi.post_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserValidationResponseConsumer {

    // Карта для хранения запросов на валидацию пользователей. Каждый запрос связан с CompletableFuture,
    // который будет завершён с результатом проверки существования пользователя.
    private final ConcurrentMap<Long, CompletableFuture<Boolean>> validationResponses = new ConcurrentHashMap<>();

    // Метод для получения CompletableFuture, который будет завершён, когда придёт ответ на запрос.
    // Добавляем запрос на валидацию пользователя в карту.
    public CompletableFuture<Boolean> getUserValidationResponse(Long userId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        // Добавляем CompletableFuture для данного userId в карту для асинхронного ожидания ответа
        validationResponses.put(userId, future);
        return future; // Возвращаем CompletableFuture, чтобы вызывать методы для обработки результата
    }

    // Слушатель Kafka для получения ответа на запрос о валидации пользователя
    @KafkaListener(topics = "user-validation-response", groupId = "post-service-group")
    public void processValidationResponse(ConsumerRecord<String, String> record) {
        // Извлекаем userId (ключ сообщения) и результат валидации (значение сообщения)
        Long userId = Long.valueOf(record.key());
        Boolean exists = Boolean.valueOf(record.value());

        // Получаем асинхронный запрос (CompletableFuture) для данного userId из карты
        CompletableFuture<Boolean> future = validationResponses.remove(userId);

        // Если запрос существует, завершаем его результатом (true/false)
        if (future != null) {
            future.complete(exists);
        }
    }
}
