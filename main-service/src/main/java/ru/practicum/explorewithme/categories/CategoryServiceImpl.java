package ru.practicum.explorewithme.categories;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.events.EventRepository;
import ru.practicum.explorewithme.exception.EventsException;
import ru.practicum.explorewithme.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        return CategoryMapper.categoryToCategoryDto(categoryRepository.save(CategoryMapper
                .categoryDtoToCategory(categoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        if (!categoryRepository.existsById(categoryDto.getId())) {
            throw new NotFoundException("Категория с id-" + categoryDto.getId() + " не найдена");
        }
        return CategoryMapper.categoryToCategoryDto(categoryRepository.save(CategoryMapper
                .categoryDtoToCategory(categoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Категория с id-" + categoryId + " не найдена");
        }
        Long numberOfRelatedEvents = eventRepository.countByCategory_Id(categoryId);
        if (numberOfRelatedEvents != 0) {
            throw new EventsException("Категорию нельзя удалить, т.к. с ней связанно " + numberOfRelatedEvents
                    + " событий");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        return CategoryMapper.categoriesToCategoriesDto(categoryRepository
                .findAll(PageRequest.of(from, size)).getContent());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.categoryToCategoryDto(categoryRepository.findById(categoryId)
                .orElseThrow( () -> new NotFoundException("Категория с id-" + categoryId + " не найдена")));
    }
}
