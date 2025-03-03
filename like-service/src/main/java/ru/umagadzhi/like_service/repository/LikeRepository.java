package ru.umagadzhi.like_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.like_service.entities.Like;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // Проверяет, существует ли лайк от конкретного пользователя к посту
    boolean existsLikeByPostIdAndUserId(Long postId, Long userId);

    // Удаляет лайк пользователя у поста
    void deleteByPostIdAndUserId(Long postId, Long userId);

    // Получает все лайки определённого поста
    List<Like> findByPostId(Long postId);

    // Подсчитывает количество лайков у поста
    long countByPostId(Long postId);
}
