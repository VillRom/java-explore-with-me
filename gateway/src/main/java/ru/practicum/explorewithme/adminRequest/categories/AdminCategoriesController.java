package ru.practicum.explorewithme.adminRequest.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.adminRequest.categories.dto.CategoryDto;
import ru.practicum.explorewithme.client.AdminClient;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
public class AdminCategoriesController {

    private final AdminClient adminClient;

    @PostMapping
    public ResponseEntity<Object> addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Create new Category {}", categoryDto.getName());
        return adminClient.addCategoryAdmin(categoryDto);
    }

    @PatchMapping
    public ResponseEntity<Object> updateCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Update category {}", categoryDto);
        return adminClient.updateCategoryAdmin(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long catId) {
        log.info("Delete category by id {}", catId);
        return adminClient.deleteCategoryAdmin(catId);
    }
}
