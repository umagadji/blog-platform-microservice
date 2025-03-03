package ru.umagadzhi.like_service.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.umagadzhi.like_service.dto.*;
import ru.umagadzhi.like_service.entities.Like;
import ru.umagadzhi.like_service.kafka.*;
import ru.umagadzhi.like_service.repository.LikeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserValidationProducer userValidationProducer;
    private final UserValidationResponseConsumer userValidationResponseConsumer;
    private final PostValidationProducer postValidationProducer;
    private final PostValidationResponseConsumer postValidationResponseConsumer;
    private final UserInfoRequestProducer userInfoRequestProducer;
    private final UserInfoResponseStorage userInfoResponseStorage;

    public LikeService(LikeRepository likeRepository,
                       UserValidationProducer userValidationProducer,
                       UserValidationResponseConsumer userValidationResponseConsumer,
                       PostValidationProducer postValidationProducer,
                       PostValidationResponseConsumer postValidationResponseConsumer,
                       UserInfoRequestProducer userInfoRequestProducer,
                       UserInfoResponseStorage userInfoResponseStorage) {
        this.likeRepository = likeRepository;
        this.userValidationProducer = userValidationProducer;
        this.userValidationResponseConsumer = userValidationResponseConsumer;
        this.postValidationProducer = postValidationProducer;
        this.postValidationResponseConsumer = postValidationResponseConsumer;
        this.userInfoRequestProducer = userInfoRequestProducer;
        this.userInfoResponseStorage = userInfoResponseStorage;
    }

    //Метод для добавления лайка
    public LikeResponse addLike(LikeRequest likeRequest) {
        //Отправляет запрос в микросервис user-service для проверки существования пользователя
        userValidationProducer.validateUser(likeRequest.userId());
        //Ожидаем ответа от Kafka
        Boolean userExists;
        try {
            userExists = userValidationResponseConsumer.getUserValidationResponse(likeRequest.userId()).get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("Ответ от user-service не получен вовремя", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке пользователя", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!userExists) {
            throw new IllegalArgumentException("Пользователь с ID " + likeRequest.userId() + " не существует.");
        }

        //Отправяем запрос в микросервис post-service для проверки существования поста
        postValidationProducer.validatePost(likeRequest.postId());
        //Ожидаем ответа от Kafka
        Boolean postExists;
        try {
            postExists = postValidationResponseConsumer.getPostValidationResponse(likeRequest.postId()).get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("Ответ от post-service не получен вовремя", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке поста", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!postExists) {
            throw new IllegalArgumentException("Пост с ID " + likeRequest.postId() + " не существует.");
        }

        // Проверяем, не поставил ли пользователь лайк ранее
        if (likeRepository.existsLikeByPostIdAndUserId(likeRequest.postId(), likeRequest.userId())) {
            throw new IllegalStateException("Вы уже лайкнули этот пост");
        }

        Like like = new Like();
        like.setPostId(likeRequest.postId());
        like.setUserId(likeRequest.userId());

        Like savedLike = likeRepository.save(like);

        return new LikeResponse(savedLike.getId(), savedLike.getUserId());
    }

    //Удаление лайка у поста
    @Transactional
    public void removeLike(Long postId, Long userId) {
        //Отправляет запрос в микросервис user-service для проверки существования пользователя
        userValidationProducer.validateUser(userId);
        //Ожидаем ответа от Kafka
        Boolean userExists;
        try {
            userExists = userValidationResponseConsumer.getUserValidationResponse(userId).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке пользователя", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!userExists) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не существует.");
        }

        //Отправяем запрос в микросервис post-service для проверки существования поста
        postValidationProducer.validatePost(postId);
        //Ожидаем ответа от Kafka
        Boolean postExists;
        try {
            postExists = postValidationResponseConsumer.getPostValidationResponse(postId).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке поста", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!postExists) {
            throw new IllegalArgumentException("Пост с ID " + postId + " не существует.");
        }

        // Проверяем, есть ли лайк перед удалением
        if (!likeRepository.existsLikeByPostIdAndUserId(postId, userId)) {
            throw new IllegalStateException("Лайка не существует");
        }

        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    //Получить количество лайков у поста
    public long getLikesCount(Long postId) {
        //Отправяем запрос в микросервис post-service для проверки существования поста
        postValidationProducer.validatePost(postId);
        //Ожидаем ответа от Kafka
        Boolean postExists;
        try {
            postExists = postValidationResponseConsumer.getPostValidationResponse(postId).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке поста", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!postExists) {
            throw new IllegalArgumentException("Пост с ID " + postId + " не существует.");
        }

        return likeRepository.countByPostId(postId);
    }

    //Получить список пользователей лайкнувших пост
    public List<UserResponse> getUsersWhoLikedPost(Long postId) {
        //Отправяем запрос в микросервис post-service для проверки существования поста
        postValidationProducer.validatePost(postId);
        //Ожидаем ответа от Kafka
        Boolean postExists;
        try {
            postExists = postValidationResponseConsumer.getPostValidationResponse(postId).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке поста", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!postExists) {
            throw new IllegalArgumentException("Пост с ID " + postId + " не существует.");
        }

        // Получаем лайки на пост
        List<Like> likes = likeRepository.findByPostId(postId);
        List<Long> userIds = likes.stream().map(Like::getUserId).toList();

        System.out.println("userIds = "+userIds);

        // Если лайков нет, возвращаем пустой список
        if (userIds.isEmpty()) {
            return List.of();
        }

        // Создаём CompletableFuture для ответа
        UserInfoRequest request = new UserInfoRequest(userIds, postId);
        CompletableFuture<UserInfoResponse> futureResponse = userInfoResponseStorage.createResponseFuture(postId);

        // Отправляем запрос в Kafka
        userInfoRequestProducer.sendUserInfoRequest(request);

        try {
            // Ожидаем ответа (до 5 секунд)
            UserInfoResponse response = futureResponse.get(5, TimeUnit.SECONDS);
            return response.getUsers();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Ошибка получения данных о пользователях", e);
        }

        //return null;
    }
}
