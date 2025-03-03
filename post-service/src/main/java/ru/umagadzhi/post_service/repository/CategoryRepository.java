package ru.umagadzhi.post_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.post_service.entities.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
