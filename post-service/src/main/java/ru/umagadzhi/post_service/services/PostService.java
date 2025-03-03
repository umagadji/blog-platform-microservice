package ru.umagadzhi.post_service.services;

import org.springframework.stereotype.Service;
import ru.umagadzhi.post_service.dto.CategoryResponse;
import ru.umagadzhi.post_service.dto.PostRequest;
import ru.umagadzhi.post_service.dto.PostResponse;
import ru.umagadzhi.post_service.entities.Category;
import ru.umagadzhi.post_service.entities.Post;
import ru.umagadzhi.post_service.kafka.UserValidationProducer;
import ru.umagadzhi.post_service.kafka.UserValidationResponseConsumer;
import ru.umagadzhi.post_service.repository.CategoryRepository;
import ru.umagadzhi.post_service.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserValidationProducer userValidationProducer;
    private final UserValidationResponseConsumer responseConsumer;

    // Конструктор с зависимостью для postRepository,userRepository,categoryRepository
    public PostService(PostRepository postRepository, CategoryRepository categoryRepository,
                       UserValidationProducer userValidationProducer, UserValidationResponseConsumer responseConsumer) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.userValidationProducer = userValidationProducer;
        this.responseConsumer = responseConsumer;
    }

    //Добавление нового поста в БД
    public PostResponse createPost(PostRequest postRequest) {
        // Проверка, что id не передается для создания нового поста
        if (postRequest.getId() != null) {
            throw new IllegalArgumentException("Нельзя передавать id при создании поста.");
        }

        // Отправляем запрос на валидацию пользователя
        userValidationProducer.validateUser(postRequest.getAuthorId());
        // Ожидаем ответа от Kafka
        Boolean userExists;
        try {
            userExists = responseConsumer.getUserValidationResponse(postRequest.getAuthorId()).get();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при проверке пользователя", e);
        }
        if (!userExists) {
            throw new IllegalArgumentException("Пользователь с ID " + postRequest.getAuthorId() + " не существует.");
        }

        Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категория с id = " + postRequest.getCategoryId() + " не найдена."));

        //Создаем новый пост
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setAuthorId(postRequest.getAuthorId());
        post.setCategory(category);

        // Сохраняем пост в базе данных
        Post savedPost = postRepository.save(post);

        // Возвращаем ответ только что созданного поста
        return new PostResponse(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                //Используем UserResponse, чтобы в ответе скрыть важные данные, например пароль
                post.getAuthorId(),
                //Здесь можно было бы с category_id, пока оставил так
                new CategoryResponse(category.getId(), category.getName())
        );
    }

    //Обновление поста
    public PostResponse updatePost(PostRequest postRequest) {
        if (postRequest.getId() == null) {
            throw new IllegalArgumentException("ID поста обязателен");
        }

        if (postRequest.getTitle() == null || postRequest.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название поста обязательно");
        }

        if (postRequest.getContent() == null || postRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Контент для поста обязателен");
        }

        if (postRequest.getAuthorId() == null) {
            throw new IllegalArgumentException("Автор обязателен для поста");
        }

        if (postRequest.getCategoryId() == null) {
            throw new IllegalArgumentException("Категория обязательна для поста");
        }

        // Отправляем запрос на валидацию пользователя
        userValidationProducer.validateUser(postRequest.getAuthorId());
        // Ожидаем ответа от Kafka
        Boolean userExists;
        try {
            userExists = responseConsumer.getUserValidationResponse(postRequest.getAuthorId()).get();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при проверке пользователя", e);
        }
        if (!userExists) {
            throw new IllegalArgumentException("Пользователь с ID " + postRequest.getAuthorId() + " не существует.");
        }

        Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категория с id = " + postRequest.getCategoryId() + " не найдена."));

        //Ищем пост по id
        return postRepository.findById(postRequest.getId())
                .map(post -> {
                    //Обновляем данные поста
                    post.setTitle(postRequest.getTitle());
                    post.setContent(postRequest.getContent());
                    post.setAuthorId(postRequest.getAuthorId());
                    post.setCategory(category);

                    // Сохраняем обновленный пост в базе данных
                    Post updatedPost = postRepository.save(post);

                    //Возвращаем обновленный пост
                    return new PostResponse(
                            updatedPost.getId(),
                            updatedPost.getTitle(),
                            updatedPost.getContent(),
                            post.getAuthorId(),
                            new CategoryResponse(category.getId(), category.getName())
                            );
                }).orElse(null); //Если пост с таким id не найден
    }

    //Получить пост по его id
    public PostResponse getPostById(Long id) {
        // Ищем пост, если нет - выбрасываем исключение
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пост с id = " + id + " не найден."));

        //Возвращаем DTO объект поста
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorId(),
                new CategoryResponse(
                        post.getCategory().getId(),
                        post.getCategory().getName()
                )
        );
    }

    //Удаляем пост по его ID
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    //Получаем список постов по названию категории
    public List<PostResponse> getPostsByCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории обязательно");
        }

        List<Post> postList = postRepository.findByCategory_Name(categoryName);

        if (postList.isEmpty()) {
            throw new IllegalArgumentException("Нет постов в данной категории");
        }

        //Преобразуем в PostResponse в возвращаем список
        return postList.stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthorId(),
                        new CategoryResponse(
                                post.getCategory().getId(),
                                post.getCategory().getName()
                        )
                )).collect(Collectors.toList());

    }

    //Получаем список постов по автору
    public List<PostResponse> getPostsByAuthor(Long authorId) {
        if (authorId == null) {
            throw new IllegalArgumentException("Автор обязателен");
        }

        List<Post> postList = postRepository.findByAuthorId(authorId);

        if (postList.isEmpty()) {
            throw new IllegalArgumentException("Нет постов у этого автора");
        }

        //Преобразуем в PostResponse в возвращаем список
        return postList.stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthorId(),
                        new CategoryResponse(
                                post.getCategory().getId(),
                                post.getCategory().getName()
                        )
                )).collect(Collectors.toList());
    }

    //Получаем посты по категории и автору
    public List<PostResponse> getPostsByCategoryNameAndAuthor(String categoryName, Long authorId) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория обязательна");
        }

        if (authorId == null) {
            throw new IllegalArgumentException("Автор обязателен");
        }

        List<Post> postList = postRepository.findByCategory_NameAndAuthorId(categoryName, authorId);

        if (postList.isEmpty()) {
            throw new IllegalArgumentException("Нет постов по этой категории или автору");
        }

        //Преобразуем в PostResponse в возвращаем список
        return postList.stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthorId(),
                        new CategoryResponse(
                                post.getCategory().getId(),
                                post.getCategory().getName()
                        )
                )).collect(Collectors.toList());
    }

    //Получаем все посты
    public List<PostResponse> getAllPosts() {
        // Находим все категории в базе и преобразуем в CategoryResponse
        return postRepository.findAll().stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthorId(),
                        new CategoryResponse(
                                post.getCategory().getId(),
                                post.getCategory().getName()
                        )
                ))
                .toList();
    }

}