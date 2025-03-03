package ru.umagadzhi.post_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.umagadzhi.post_service.dto.PostRequest;
import ru.umagadzhi.post_service.dto.PostResponse;
import ru.umagadzhi.post_service.services.PostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping() // Обработчик POST-запросов для создания поста
    public ResponseEntity<PostResponse> createPost(@RequestBody @Valid PostRequest postRequest) {
        //Вызываем сервис для создания поста и получения ответа
        PostResponse postResponse = postService.createPost(postRequest);

        //Возвращаем ответ
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @PutMapping()// Обработчик PUT-запросов для обновление поста
    public ResponseEntity<Object> updatePost(@RequestBody @Valid PostRequest postRequest) {
        //Вызов сервиса для обновления поста
        PostResponse updatedPost = postService.updatePost(postRequest);

        if (updatedPost == null) {
            //Если не найден пост
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Пост не найден"));
        }

        //Если пост найден
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/{id}") //Получение поста по его ID
    public ResponseEntity<Object> getPostById(@PathVariable Long id) {
        PostResponse postResponse = postService.getPostById(id);

        if (postResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Категория не найден"));
        }

        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePostById(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(Map.of("message", "Пост удален"));
    }

    @GetMapping("/category")
    public ResponseEntity<List<PostResponse>> getPostsByCategoryName(@RequestParam String categoryName) {
        List<PostResponse> postResponseList = postService.getPostsByCategory(categoryName);

        return ResponseEntity.ok(postResponseList);
    }

    @GetMapping("/author")
    public ResponseEntity<List<PostResponse>> getPostsByAuthor(@RequestParam String author) {
        List<PostResponse> postResponseList = postService.getPostsByAuthor(1L);

        return ResponseEntity.ok(postResponseList);
    }

    @GetMapping() //Получаем посты по автору, категории или все
    public ResponseEntity<List<PostResponse>> getPostByFilter(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author) {

        List<PostResponse> posts;

        if (category != null && author != null) {
            posts = postService.getPostsByCategoryNameAndAuthor(category, 1L);
        } else if (category != null) {
            posts = postService.getPostsByCategory(category);
        } else if (author != null) {
            posts = postService.getPostsByAuthor(1L);
        } else {
            posts = postService.getAllPosts();
        }

        return ResponseEntity.ok(posts);
    }
}
