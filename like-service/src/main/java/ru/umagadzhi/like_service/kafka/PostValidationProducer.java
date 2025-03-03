package ru.umagadzhi.like_service.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostValidationProducer {
    // KafkaTemplate для отправки сообщений в Kafka
    public final KafkaTemplate<String, String> kafkaTemplate;

    //Топик для отправки сообщений в Kafka
    private static final String TOPIC = "post-validation-request";

    public PostValidationProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Метод для отправки запроса на валидацию поста
    public void validatePost(Long postId)  {
        // Отправляем ID пользователя в топик "post-validation-request"
        // Значение сообщения - строковое представление ID поста
        kafkaTemplate.send(TOPIC, postId.toString());
    }
}
