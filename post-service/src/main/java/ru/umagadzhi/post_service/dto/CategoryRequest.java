package ru.umagadzhi.post_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//Класс описывающий запросы для CRUD операций категории
public class CategoryRequest {
    private Long id;
    @NotBlank(message = "Название категории обязательно")
    private String name;
}
