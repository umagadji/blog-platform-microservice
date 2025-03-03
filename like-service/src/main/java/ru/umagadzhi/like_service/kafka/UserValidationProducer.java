package ru.umagadzhi.like_service.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service //Сервис для отправки сообщений в Kafka по пользователю
public class UserValidationProducer {
    // KafkaTemplate для отправки сообщений в Kafka
    public final KafkaTemplate<String, String> stringKafkaTemplate;

    //Топик для отправки сообщений в Kafka
    private static final String TOPIC = "user-validation-request";

    public UserValidationProducer(KafkaTemplate<String, String> stringKafkaTemplate) {
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    // Метод для отправки запроса на валидацию пользователя
    public void validateUser(Long userId)  {
        // Отправляем ID пользователя в топик "user-validation-request"
        // Значение сообщения - строковое представление ID пользователя
        stringKafkaTemplate.send(TOPIC, userId.toString());
    }
}
