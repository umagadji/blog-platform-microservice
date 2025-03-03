package ru.umagadzhi.like_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service // Аннотация @Service указывает, что этот класс является Spring-сервисом
public class PostValidationResponseConsumer {

    // Потокобезопасная карта для хранения запросов на валидацию поста.
    // Ключ — postId, значение — CompletableFuture, которое завершится, когда придёт ответ от post-service.
    private final ConcurrentMap<Long, CompletableFuture<Boolean>> validationResponses = new ConcurrentHashMap<>();

    /**
     * Метод создаёт новый CompletableFuture и добавляет его в карту ожидания ответов.
     * Вызывается, когда comment-service запрашивает валидацию поста.
     *
     * @param postId ID поста, которого нужно проверить.
     * @return CompletableFuture, который завершится, когда придёт ответ от post-service.
     */
    public CompletableFuture<Boolean> getPostValidationResponse(Long postId) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        validationResponses.put(postId, completableFuture); // Добавляем в карту ожидания
        return completableFuture;
    }

    /**
     * Kafka Listener, который получает сообщения из топика "post-validation-response".
     * Слушатель обрабатывает ответ о валидации пользователя, пришедший от post-service.
     *
     * @param record Сообщение Kafka, содержащее postId (ключ) и результат валидации (значение).
     */
    @KafkaListener(topics = "post-validation-response", groupId = "like-service-group")
    public void processValidationResponse(ConsumerRecord<String, String> record) {
        // Извлекаем userId (ключ сообщения) и результат валидации (значение)
        Long postId = Long.valueOf(record.key());
        Boolean exists = Boolean.valueOf(record.value());

        // Получаем CompletableFuture из карты и удаляем его
        CompletableFuture<Boolean> completableFuture = validationResponses.remove(postId);

        // Если запрос на валидацию существует, завершаем его результатом (true/false)
        if (completableFuture != null) {
            completableFuture.complete(exists);
        }
    }
}