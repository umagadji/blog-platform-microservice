package ru.umagadzhi.post_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    // Вместо объекта User используем ID
    private Long authorId;

    @ManyToOne
    @JoinColumn(name = "category_id") // Добавляем ссылку на категорию
    private Category category;

    // Вместо объектов Comment и Like используем списки их ID
    @ElementCollection
    private List<Long> commentIds = new ArrayList<>();

    @ElementCollection
    private List<Long> likeIds = new ArrayList<>();
}
