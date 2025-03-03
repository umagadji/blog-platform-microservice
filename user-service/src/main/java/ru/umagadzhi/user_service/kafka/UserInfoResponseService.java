package ru.umagadzhi.user_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.umagadzhi.user_service.dto.UserInfoRequest;
import ru.umagadzhi.user_service.dto.UserInfoResponse;
import ru.umagadzhi.user_service.dto.UserResponse;
import ru.umagadzhi.user_service.repository.UserRepository;

import java.util.List;

@Service
public class UserInfoResponseService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserInfoResponse> kafkaTemplate;

    private String RESPONSE_TOPIC = "user-info-response";

    public UserInfoResponseService(UserRepository userRepository, KafkaTemplate<String, UserInfoResponse> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "user-info-request", groupId = "user-service-group", containerFactory = "userInfoRequestKafkaListenerFactory")
    public void consumeUserInfoRequest(UserInfoRequest request) {
        List<UserResponse> users = userRepository.findAllById(request.getUserIds())
                .stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail()))
                .toList();

        UserInfoResponse response = new UserInfoResponse(request.getPostId(), users);
        kafkaTemplate.send(RESPONSE_TOPIC, response);
    }

}
