package ru.umagadzhi.comment_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.umagadzhi.comment_service.dto.CommentRequest;
import ru.umagadzhi.comment_service.dto.CommentResponse;
import ru.umagadzhi.comment_service.services.CommentService;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping()//Создание комментария
    public ResponseEntity<CommentResponse> createComment(@RequestBody @Valid CommentRequest commentRequest) {
        //Вызываем сервис для создания комментария и получения ответа
        CommentResponse commentResponse = commentService.createComment(commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @PutMapping()//Обновление комментария
    public ResponseEntity<Object> updateComment(@RequestBody @Valid CommentRequest commentRequest) {
        //Вызов сервиса для обновления комментария
        CommentResponse updatedComment = commentService.updateComment(commentRequest);

        if (updatedComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Комментарий не найден"));
        }

        return ResponseEntity.ok(updatedComment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCommentById(@PathVariable Long id) {
        CommentResponse commentResponse = commentService.getCommentByID(id);

        if (commentResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Комментарий не найден"));
        }

        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCommentById(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().body(Map.of("error", "Комментарий удален"));
    }
}
