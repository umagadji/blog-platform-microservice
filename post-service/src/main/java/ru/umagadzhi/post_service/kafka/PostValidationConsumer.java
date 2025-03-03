package ru.umagadzhi.post_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.umagadzhi.post_service.repository.PostRepository;

@Service //Сервис для отправки сообщений в Kafka для
public class PostValidationConsumer {

    //Репозиторий для проверки постов
    private final PostRepository postRepository;
    //KafkaTemplate для отправки сообщений в kafka
    private final KafkaTemplate<String, String> kafkaTemplate;
    //Название топика, в который отправляем результат проверки
    private static final String RESPONSE_TOPIC = "post-validation-response";

    // Конструктор, через который инжектируются зависимости
    public PostValidationConsumer(PostRepository postRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.postRepository = postRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    //Слушатель kafka для получения запросов на валидацию post
    @KafkaListener(topics = "post-validation-request", groupId = "post-service-group")
    public void validatePost(ConsumerRecord<String, String> record) {
        //Извлекаем id поста из сообщения Kafka (Это строка, представляющая Long)
        Long postId = Long.valueOf(record.value());

        //Проверяем, существует ли пост с таким id в БД
        boolean exists = postRepository.existsById(postId);

        //Отправляем результат проверки в топик "post-validation-response"
        // Ключ сообщения - id поста, значение - строковое представление результата проверки (true/false)
        kafkaTemplate.send(RESPONSE_TOPIC, postId.toString(), Boolean.toString(exists));
    }
}
