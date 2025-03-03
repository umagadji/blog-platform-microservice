package ru.umagadzhi.post_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserValidationProducer {

    // KafkaTemplate для отправки сообщений в Kafka
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Название топика, в который будут отправляться запросы на валидацию пользователя
    private static final String TOPIC = "user-validation-request";

    // Конструктор, через который инжектируется KafkaTemplate
    public UserValidationProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Метод для отправки запроса на валидацию пользователя
    public void validateUser(Long userId) {
        // Отправляем ID пользователя в топик "user-validation-request"
        // Значение сообщения - строковое представление ID пользователя
        kafkaTemplate.send(TOPIC, userId.toString());
    }
}
