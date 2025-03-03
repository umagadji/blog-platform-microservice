package ru.umagadzhi.post_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//Класс описывающий ответы для CRUD операций категории
public class CategoryResponse {
    private Long id;
    private String name;
}
