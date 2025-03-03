package ru.umagadzhi.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.user_service.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String text);
    Optional<User> findByEmail(String text);
}
