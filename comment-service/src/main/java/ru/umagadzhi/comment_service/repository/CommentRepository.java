package ru.umagadzhi.comment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.comment_service.entities.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}
