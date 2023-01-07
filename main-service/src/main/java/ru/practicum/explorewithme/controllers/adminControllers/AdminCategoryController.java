package ru.practicum.explorewithme.controllers.adminControllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.categories.CategoryService;
import ru.practicum.explorewithme.categories.dto.CategoryDto;


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
        log.info("Create new Category {}", categoryDto.getName());
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Update category {}", categoryDto);
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        log.info("Delete category by id {}", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}
