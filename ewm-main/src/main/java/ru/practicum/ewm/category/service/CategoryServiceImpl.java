package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.dto.UpdateCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;


    @Override
    @Transactional
    public CategoryDto add(NewCategoryDto dto) {

        if (repository.existsByName(dto.getName())) {
            throw new DataConflictException("Попытка изменения имени категории на уже существующее");
        }
        Category saved = repository.save(mapper.toCategoryModel(dto));
        return mapper.toCategoryDto(saved);
    }

    @Override
    @Transactional
    public CategoryDto update(int dtoId, UpdateCategoryDto dto) {
        Category cat = findByIdOrThrow(dtoId);
        if (repository.existsByName(dto.getName()) && !dto.getName().equals(cat.getName())) {
            throw new DataConflictException("Попытка изменения имени категории на уже существующее");
        }
        Category updated = repository.save(mapper.toCategoryModel(dto));
        return mapper.toCategoryDto(updated);

    }

    @Override
    @Transactional
    public void delete(int dtoId) {
        Category category = findByIdOrThrow(dtoId);
        if (eventRepository.existsByCategoryName(category.getName())) {
            throw new DataConflictException("В категории есть события");
        }

        repository.delete(category);
    }

    @Override
    public CategoryDto get(int id) {
        return mapper.toCategoryDto(findByIdOrThrow(id));
    }


    @Override
    public List<CategoryDto> get(int from, int size) {
        int pageNumber = from >= size ? from / size : 0;
        PageRequest pageRequest = PageRequest.of(pageNumber, size);
        return repository.findAll(pageRequest).getContent().stream()
                .map(mapper::toCategoryDto)
                .toList();
    }

    private Category findByIdOrThrow(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Категория не найдена"));
    }


}
