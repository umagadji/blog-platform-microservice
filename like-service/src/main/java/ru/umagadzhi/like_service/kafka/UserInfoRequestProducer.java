package ru.umagadzhi.like_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.umagadzhi.like_service.dto.UserInfoRequest;

@Service
public class UserInfoRequestProducer {

    private final KafkaTemplate<String, UserInfoRequest> kafkaTemplate;

    public UserInfoRequestProducer(KafkaTemplate<String, UserInfoRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserInfoRequest(UserInfoRequest request) {
        String REQUEST_TOPIC = "user-info-request";
        kafkaTemplate.send(REQUEST_TOPIC, request);
    }
}
