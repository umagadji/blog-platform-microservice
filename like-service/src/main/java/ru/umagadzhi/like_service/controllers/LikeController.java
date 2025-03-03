package ru.umagadzhi.like_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.umagadzhi.like_service.dto.LikeRequest;
import ru.umagadzhi.like_service.dto.LikeResponse;
import ru.umagadzhi.like_service.dto.UserResponse;
import ru.umagadzhi.like_service.services.LikeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping()//Добавляет лайк к посту
    public ResponseEntity<LikeResponse> addLike(@RequestBody @Valid LikeRequest likeRequest) {
        LikeResponse likeResponse = likeService.addLike(likeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeResponse);
    }

    @DeleteMapping()//Удаляет лайк у поста
    public ResponseEntity<Object> deleteLike(@RequestParam Long postId, @RequestParam Long userId) {
        likeService.removeLike(postId, userId);
        return ResponseEntity.ok().body(Map.of("message", "лайк удален"));
    }

    @GetMapping("/count")//Количество лайков у поста
    public ResponseEntity<Long> getLikesCount(@RequestParam Long postId) {
        Long count = likeService.getLikesCount(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/users")//Список пользователей, лайкнувших пост
    public ResponseEntity<List<UserResponse>> getUsersWhoLikedPost(Long postId) {
        List<UserResponse> userResponseList = likeService.getUsersWhoLikedPost(postId);
        return ResponseEntity.ok(userResponseList);
    }

}
