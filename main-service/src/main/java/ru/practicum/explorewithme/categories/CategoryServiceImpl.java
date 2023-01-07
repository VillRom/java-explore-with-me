package ru.practicum.explorewithme.categories;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.events.EventRepository;
import ru.practicum.explorewithme.exception.EventsException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.RequestException;

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
        if (categoryDto.getName() == null) {
            throw new RequestException("Поле name не должно быть пустым");
        }
        return CategoryMapper.categoryToCategoryDto(categoryRepository.save(CategoryMapper
                .categoryDtoToCategory(categoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        if (categoryDto.getId() == null || categoryDto.getName() == null) {
            throw new RequestException("В теле запроса не должно быть пустых полей");
        }
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
        if (eventRepository.countByCategory_Id(categoryId) != 0) {
            throw new EventsException("Категорию нельзя удалить, т.к. с ней связанно " + eventRepository
                    .countByCategory_Id(categoryId) + " событий");
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
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Категория с id-" + categoryId + " не найдена");
        }
        return CategoryMapper.categoryToCategoryDto(categoryRepository.getReferenceById(categoryId));
    }
}
