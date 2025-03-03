package ru.umagadzhi.user_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.umagadzhi.user_service.enums.Role;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//Класс описывает пользователей
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // Роль пользователя (AUTHOR, ADMIN, READER)

    //Получаем роль пользователя
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Конвертируем роль в роли пользователя для Spring Security
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    //Учетная запись не истекла
    @Override
    public boolean isAccountNonExpired() {
        return true; // Можно добавить логику для истечения аккаунта
    }

    //Аккаунт не заблокирован
    @Override
    public boolean isAccountNonLocked() {
        return true; // Можно добавить логику для блокировки аккаунта
    }

    //Пароль не устарел
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Можно добавить логику для истечения срока действия учетных данных
    }

    //Акаунт включен
    @Override
    public boolean isEnabled() {
        return true; // Можно добавить логику для проверки активности пользователя
    }
}
