package ru.umagadzhi.user_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.umagadzhi.user_service.repository.UserRepository;

@Service
public class UserValidationConsumer {

    // Репозиторий пользователей для проверки, существует ли пользователь с данным id
    private final UserRepository userRepository;

    // KafkaTemplate для отправки сообщений в Kafka
    private final KafkaTemplate<String, String> kafkaTemplateString;

    // Название топика, в который будем отправлять результат проверки
    private static final String RESPONSE_TOPIC = "user-validation-response";

    // Конструктор, через который инжектируются зависимости
    public UserValidationConsumer(UserRepository userRepository, KafkaTemplate<String, String> kafkaTemplateString) {
        this.userRepository = userRepository;
        this.kafkaTemplateString = kafkaTemplateString;
    }

    // Слушатель Kafka для получения запросов на валидацию пользователя
    @KafkaListener(topics = "user-validation-request", groupId = "user-service-group")
    public void validateUser(ConsumerRecord<String, String> record) {
        // Извлекаем id пользователя из сообщения Kafka (предполагается, что это строка, представляющая Long)
        Long userId = Long.valueOf(record.value());

        // Проверяем, существует ли пользователь с таким id в базе данных
        boolean exists = userRepository.existsById(userId);

        // Отправляем результат проверки в топик "user-validation-response"
        // Ключ сообщения - id пользователя, значение - строковое представление результата проверки (true/false)
        kafkaTemplateString.send(RESPONSE_TOPIC, userId.toString(), Boolean.toString(exists));
    }
}