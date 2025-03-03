package ru.umagadzhi.user_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.umagadzhi.user_service.dto.UserEvent;
import ru.umagadzhi.user_service.entities.User;
import ru.umagadzhi.user_service.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Метод для публикации события регистрации пользователя в Kafka
    public void publishUserRegistrationEvent(User user) {
        UserEvent userEvent = new UserEvent();
        userEvent.setId(user.getId());
        userEvent.setUsername(user.getUsername());
        userEvent.setEmail(user.getEmail());
        userEvent.setEventType("USER_REGISTERED");
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // кодируем пароль
        User savedUser = repository.save(user);

        return savedUser;
    }

    public Optional<User> findByUsername(String text) {
        return repository.findByUsername(text);
    }

    public Optional<User> findByEmail(String text) {
        return repository.findByEmail(text);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
        );
    }
}
