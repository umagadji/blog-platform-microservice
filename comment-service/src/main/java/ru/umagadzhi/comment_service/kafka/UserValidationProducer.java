package ru.umagadzhi.comment_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service //Сервис для отправки сообщений в Kafka по пользователю
public class UserValidationProducer {
    // KafkaTemplate для отправки сообщений в Kafka
    public final KafkaTemplate<String, String> kafkaTemplate;

    //Топик для отправки сообщений в Kafka
    private static final String TOPIC = "user-validation-request";

    public UserValidationProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Метод для отправки запроса на валидацию пользователя
    public void validateUser(Long userId)  {
        // Отправляем ID пользователя в топик "user-validation-request"
        // Значение сообщения - строковое представление ID пользователя
        kafkaTemplate.send(TOPIC, userId.toString());
    }
}
