package ru.umagadzhi.post_service.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.umagadzhi.post_service.dto.CategoryRequest;
import ru.umagadzhi.post_service.dto.CategoryResponse;
import ru.umagadzhi.post_service.services.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController // Аннотация для контроллера, который обрабатывает HTTP-запросы
@RequestMapping("/api/categories") // Определяет базовый путь для всех методов контроллера
public class CategoryController {

    private final CategoryService categoryService;

    // Конструктор для внедрения зависимости CategoryService
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Создание новой категории.
     *
     * @param request данные для создания категории.
     * @return объект CategoryResponse с данными только что созданной категории.
     */
    @PostMapping // Обработчик POST-запросов для создания категории
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        // Вызов сервиса для создания категории и получения ответа
        CategoryResponse categoryResponse = categoryService.createCategory(request);

        // Ответ с кодом 201 (создано), возвращаем объект с данными категории
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    /**
     * Обновление категории по ID.
     *
     * @param request данные для обновления категории.
     * @return обновленные данные категории или сообщение об ошибке, если категория не найдена.
     */
    @PutMapping // Обработчик PUT-запросов для обновления категории
    public ResponseEntity<Object> updateCategory(
            @RequestBody @Valid CategoryRequest request) { // Данные для обновления передаются в теле запроса

        // Вызов сервиса для обновления категории
        CategoryResponse updatedCategory = categoryService.updateCategory(request);

        if (updatedCategory == null) {
            // Если категория не найдена, возвращаем ошибку 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Категория не найдена"));
        }

        // Если категория обновлена, возвращаем обновленные данные категории
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Получение всех категорий.
     *
     * @return список объектов CategoryResponse для всех категорий.
     */
    @GetMapping // Обработчик GET-запросов для получения всех категорий
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        // Вызов сервиса для получения всех категорий
        List<CategoryResponse> categories = categoryService.getAllCategories();

        // Ответ с кодом 200 (ОК), возвращаем список категорий
        return ResponseEntity.ok(categories);
    }

    /**
     * Получение категории по ID.
     *
     * @param id идентификатор категории, передается как параметр запроса.
     * @return объект CategoryResponse с данными категории или сообщение об ошибке, если категория не найдена.
     */
    @GetMapping("/{id}") // Обработчик GET-запросов для получения категории по ID
    public ResponseEntity<Object> getCategoryById(@PathVariable Long id) {
        // Вызов сервиса для получения категории по ID
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);

        if (categoryResponse == null) {
            // Если категория не найдена, возвращаем ошибку 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Категория не найдена"));
        }

        // Если категория найдена, возвращаем данные категории
        return ResponseEntity.ok(categoryResponse);
    }

    /**
     * Удаление категории по ID.
     *
     * @param id идентификатор категории, передается как параметр запроса.
     * @return сообщение об успешном удалении категории.
     */
    @DeleteMapping("/{id}") // Обработчик DELETE-запросов для удаления категории
    public ResponseEntity<Object> deleteCategoryById(@PathVariable Long id) {
        // Вызов сервиса для удаления категории по ID
        categoryService.deleteCategoryById(id);

        // Ответ с кодом 200 (ОК), возвращаем сообщение о успешном удалении
        return ResponseEntity.ok(Map.of("message", "Категория удалена"));
    }
}