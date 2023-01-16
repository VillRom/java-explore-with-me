package ru.practicum.explorewithme.categories;

import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(Long categoryId);
}
