package ru.practicum.explorewithme.publicrequests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.client.PublicClient;


@RestController
@RequestMapping("/categories")
@Slf4j
@RequiredArgsConstructor
public class PublicCategoryController {

    private final PublicClient publicClient;

    @GetMapping
    public ResponseEntity<Object> getAllCategories(@RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Get all categories");
        return publicClient.getAllCategoriesPublic(from, size);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<Object> getCategoryById(@PathVariable Long catId) {
        log.info("Get category by id {}", catId);
        return publicClient.getCategoryByIdPublic(catId);
    }
}
