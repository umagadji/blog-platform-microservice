package ru.umagadzhi.post_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.post_service.entities.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long authorId);
    List<Post> findByCategory_Name(String categoryName);
    List<Post> findByCategory_NameAndAuthorId(String categoryName, Long authorId);
}
