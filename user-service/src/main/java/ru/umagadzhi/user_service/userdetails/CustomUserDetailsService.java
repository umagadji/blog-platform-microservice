package ru.umagadzhi.user_service.userdetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.umagadzhi.user_service.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService { // Реализация стандартного интерфейса Spring Security для загрузки пользователей
    @Autowired
    private UserRepository userRepository; // Инъекция репозитория пользователей для доступа к данным

    /*Метод загружает пользователя по имени (username) для аутентификации.
    Вызывается Spring Security во время аутентификации.
    * @param username Имя пользователя, переданное при входе
    * @return Объект UserDetails с информацией о пользователе
    * @throws UsernameNotFoundException Если пользователь не найден*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username) // Ищем пользователя в базе данных
                // Если пользователя нет, выбрасываем исключение
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
