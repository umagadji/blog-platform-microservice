package ru.umagadzhi.like_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.umagadzhi.like_service.dto.UserInfoRequest;
import ru.umagadzhi.like_service.dto.UserInfoResponse;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    // Consumer Factory для строковых значений
    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Десериализатор для строкового значения
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    // Kafka Listener Container Factory для строковых значений
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        return factory;
    }

    //////////////////

    // Consumer Factory для десериализации JSON в UserInfoResponse
    @Bean
    public ConsumerFactory<String, UserInfoResponse> userInfoResponseConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Разрешаем любые пакеты
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UserInfoResponse.class.getName()); //  Задаем класс явно
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(UserInfoResponse.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserInfoResponse> userInfoResponseKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserInfoResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userInfoResponseConsumerFactory());
        return factory;
    }
}