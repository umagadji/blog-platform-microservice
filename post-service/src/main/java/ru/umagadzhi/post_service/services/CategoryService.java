package ru.umagadzhi.post_service.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.umagadzhi.post_service.dto.CategoryRequest;
import ru.umagadzhi.post_service.dto.CategoryResponse;
import ru.umagadzhi.post_service.entities.Category;
import ru.umagadzhi.post_service.repository.CategoryRepository;

import java.util.List;

@Service // Обозначаем класс как сервисный компонент для Spring
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Конструктор с зависимостью для CategoryRepository
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Создает новую категорию.
     *
     * @param request объект с данными для создания категории.
     * @return CategoryResponse объект с данными только что созданной категории.
     */
    public CategoryResponse createCategory(CategoryRequest request) {
        // Проверка, что id не передается для создания новой категории
        if (request.getId() != null) {
            throw new IllegalArgumentException("Нельзя передавать id при создании категории.");
        }

        // Создаем новую категорию
        Category category = new Category();
        category.setName(request.getName());

        // Сохраняем категорию в базе данных
        Category savedCategory = categoryRepository.save(category);

        // Возвращаем ответ с id и name только что созданной категории
        return new CategoryResponse(savedCategory.getId(), savedCategory.getName());
    }

    /**
     * Обновляет категорию по id.
     *
     * @param request данные для обновления категории.
     * @return обновленная категория в виде CategoryResponse или null, если категория с таким id не найдена.
     */
    public CategoryResponse updateCategory(CategoryRequest request) {

        // Проверяем, передан ли ID
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID категории не может быть пустым");
        }

        // Проверяем, передано ли название
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым");
        }

        // Ищем категорию по id
        return categoryRepository.findById(request.getId())
                .map(category -> {
                    // Обновляем данные категории
                    category.setName(request.getName());

                    // Сохраняем обновленную категорию в базе данных
                    Category updatedCategory = categoryRepository.save(category);

                    // Возвращаем обновленную категорию
                    return new CategoryResponse(updatedCategory.getId(), updatedCategory.getName());
                })
                .orElse(null); // Если категория с таким id не найдена, возвращаем null
    }

    /**
     * Получает список всех категорий.
     *
     * @return список всех категорий в виде объектов CategoryResponse.
     */
    public List<CategoryResponse> getAllCategories() {
        // Находим все категории в базе и преобразуем в CategoryResponse
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .toList();
    }

    /**
     * Получает категорию по id.
     *
     * @param id идентификатор категории.
     * @return объект CategoryResponse или null, если категория с таким id не найдена.
     */
    public CategoryResponse getCategoryById(Long id) {
        // Находим категорию по id, если она существует, преобразуем в CategoryResponse
        return categoryRepository.findById(id)
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .orElse(null);
    }

    /**
     * Удаляет категорию по id.
     *
     * @param id идентификатор категории.
     */
    public void deleteCategoryById(Long id) {
        // Удаляем категорию по id
        categoryRepository.deleteById(id);
    }
}