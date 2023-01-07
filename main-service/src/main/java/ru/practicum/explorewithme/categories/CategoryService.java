package ru.practicum.explorewithme.categories;

import ru.practicum.explorewithme.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(Long categoryId);
}
