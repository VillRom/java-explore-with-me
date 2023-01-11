package ru.practicum.explorewithme.categories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.categories.CategoryService;
import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.exception.RequestException;


@RestController
@RequestMapping("/admin/categories")
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public CategoryDto addCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto.getName() == null) {
            throw new RequestException("Поле name не должно быть пустым");
        }
        log.info("Create new Category {}", categoryDto.getName());
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto.getId() == null || categoryDto.getName() == null) {
            throw new RequestException("В теле запроса не должно быть пустых полей");
        }
        log.info("Update category {}", categoryDto);
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        log.info("Delete category by id {}", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}
