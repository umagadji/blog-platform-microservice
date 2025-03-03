package ru.umagadzhi.comment_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service // Аннотация @Service указывает, что этот класс является Spring-сервисом
public class UserValidationResponseConsumer {

    // Потокобезопасная карта для хранения запросов на валидацию пользователей.
    // Ключ — userId, значение — CompletableFuture, которое завершится, когда придёт ответ от user-service.
    private final ConcurrentMap<Long, CompletableFuture<Boolean>> validationResponses = new ConcurrentHashMap<>();

    /**
     * Метод создаёт новый CompletableFuture и добавляет его в карту ожидания ответов.
     * Вызывается, когда comment-service запрашивает валидацию пользователя.
     *
     * @param userId ID пользователя, которого нужно проверить.
     * @return CompletableFuture, который завершится, когда придёт ответ от user-service.
     */
    public CompletableFuture<Boolean> getUserValidationResponse(Long userId) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        validationResponses.put(userId, completableFuture); // Добавляем в карту ожидания
        return completableFuture;
    }

    /**
     * Kafka Listener, который получает сообщения из топика "user-validation-response".
     * Слушатель обрабатывает ответ о валидации пользователя, пришедший от user-service.
     *
     * @param record Сообщение Kafka, содержащее userId (ключ) и результат валидации (значение).
     */
    @KafkaListener(topics = "user-validation-response", groupId = "comment-service-group")
    public void processValidationResponse(ConsumerRecord<String, String> record) {
        // Извлекаем userId (ключ сообщения) и результат валидации (значение)
        Long userId = Long.valueOf(record.key());
        Boolean exists = Boolean.valueOf(record.value());

        // Получаем CompletableFuture из карты и удаляем его
        CompletableFuture<Boolean> completableFuture = validationResponses.remove(userId);

        // Если запрос на валидацию существует, завершаем его результатом (true/false)
        if (completableFuture != null) {
            completableFuture.complete(exists);
        }
    }
}