package ru.umagadzhi.post_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category") // Сохраняем связь с постами
    @JsonIgnore //Чтобы исключить из вывода в ответе в json. Игнорируем список постов, чтобы избежать рекурсии
    private Set<Post> posts = new HashSet<>();
}
