package ru.umagadzhi.comment_service.services;

import org.springframework.stereotype.Service;
import ru.umagadzhi.comment_service.dto.CommentRequest;
import ru.umagadzhi.comment_service.dto.CommentResponse;
import ru.umagadzhi.comment_service.entities.Comment;
import ru.umagadzhi.comment_service.kafka.PostValidationProducer;
import ru.umagadzhi.comment_service.kafka.PostValidationResponseConsumer;
import ru.umagadzhi.comment_service.kafka.UserValidationProducer;
import ru.umagadzhi.comment_service.kafka.UserValidationResponseConsumer;
import ru.umagadzhi.comment_service.repository.CommentRepository;

import java.util.concurrent.ExecutionException;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserValidationProducer userValidationProducer;
    private final UserValidationResponseConsumer userValidationResponseConsumer;
    private final PostValidationProducer postValidationProducer;
    private final PostValidationResponseConsumer postValidationResponseConsumer;

    public CommentService(CommentRepository commentRepository,
                          UserValidationProducer userValidationProducer,
                          UserValidationResponseConsumer userValidationResponseConsumer,
                          PostValidationProducer postValidationProducer,
                          PostValidationResponseConsumer postValidationResponseConsumer) {
        this.commentRepository = commentRepository;
        this.userValidationProducer = userValidationProducer;
        this.userValidationResponseConsumer = userValidationResponseConsumer;
        this.postValidationProducer = postValidationProducer;
        this.postValidationResponseConsumer = postValidationResponseConsumer;
    }

    //Добавление комментария к посту
    public CommentResponse createComment(CommentRequest commentRequest) {
        // Проверка, что id не передается для создания нового комментария
        if (commentRequest.getId() != null) {
            throw new IllegalArgumentException("Нельзя передавать id при создании комментария.");
        }

        // Проверяем, передано ли содержимое комментария
        if (commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Комментарий не может быть пустым.");
        }

        //Отправляем запрос на валидацию для user
        userValidationProducer.validateUser(commentRequest.getAuthorId());
        // Ожидаем ответа от Kafka
        Boolean userExists;
        try {
            userExists = userValidationResponseConsumer.getUserValidationResponse(commentRequest.getAuthorId()).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке пользователя", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!userExists) {
            throw new IllegalArgumentException("Пользователь с ID " + commentRequest.getAuthorId() + " не существует.");
        }

        //Отправляем запрос на валидацию для post
        postValidationProducer.validatePost(commentRequest.getPostId());
        // Ожидаем ответа от Kafka
        Boolean postExists;
        try {
            postExists = postValidationResponseConsumer.getPostValidationResponse(commentRequest.getPostId()).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке поста", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!postExists) {
            throw new IllegalArgumentException("Пост с ID " + commentRequest.getPostId() + " не существует.");
        }

        //Создаем комментарий
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPostId(commentRequest.getPostId());
        comment.setAuthorId(commentRequest.getAuthorId());

        //Сохраняем комментарий
        Comment savedComment = commentRepository.save(comment);

        //Возвращаем DTO комментария
        return new CommentResponse(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getAuthorId()
        );
    }

    //Обновление комментария
    public CommentResponse updateComment(CommentRequest commentRequest) {
        if (commentRequest.getId() == null) {
            throw new IllegalArgumentException("ID комментария обязателен");
        }

        // Проверяем, передано ли содержимое комментария
        if (commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Комментарий не может быть пустым.");
        }

        if (commentRequest.getAuthorId() == null) {
            throw new IllegalArgumentException("Автор обязателен для комментария");
        }

        if (commentRequest.getPostId() == null) {
            throw new IllegalArgumentException("Пост обязателен для комментария");
        }

        //Отправляем запрос на валидацию для user
        userValidationProducer.validateUser(commentRequest.getAuthorId());
        // Ожидаем ответа от Kafka
        Boolean userExists;
        try {
            userExists = userValidationResponseConsumer.getUserValidationResponse(commentRequest.getAuthorId()).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке пользователя", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!userExists) {
            throw new IllegalArgumentException("Пользователь с ID " + commentRequest.getAuthorId() + " не существует.");
        }

        //Отправляем запрос на валидацию для post
        postValidationProducer.validatePost(commentRequest.getPostId());
        // Ожидаем ответа от Kafka
        Boolean postExists;
        try {
            postExists = postValidationResponseConsumer.getPostValidationResponse(commentRequest.getPostId()).get();
        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при проверке поста", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!postExists) {
            throw new IllegalArgumentException("Пост с ID " + commentRequest.getPostId() + " не существует.");
        }

        //Ищем комментарий по его ID
        return commentRepository.findById(commentRequest.getId())
                .map(comment -> {
                    //Обновляем комментарий
                    comment.setContent(commentRequest.getContent());
                    comment.setPostId(commentRequest.getPostId());
                    comment.setAuthorId(commentRequest.getAuthorId());

                    //Сохраняем в БД обновленный комментарий
                    Comment updatedComment = commentRepository.save(comment);

                    return new CommentResponse(
                            updatedComment.getId(),
                            updatedComment.getContent(),
                            updatedComment.getAuthorId()
                    );
                }).orElse(null); //Если комментарий не найден
    }

    //Получаем комментарий по ID
    public CommentResponse getCommentByID(Long id) {
        //Ищем комментарий
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Комментарий и id = " + id + " не найден"));

        //Возвращаем DTO объект
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorId()
        );
    }

    //Удаляем комментарий по его ID
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
