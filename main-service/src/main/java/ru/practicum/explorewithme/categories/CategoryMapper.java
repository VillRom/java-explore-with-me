package ru.practicum.explorewithme.categories;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.categories.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryMapper {

    public static CategoryDto categoryToCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public static Category categoryDtoToCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

    public static List<CategoryDto> categoriesToCategoriesDto(List<Category> categories) {
        return categories.stream().map(CategoryMapper::categoryToCategoryDto).collect(Collectors.toList());
    }
}
