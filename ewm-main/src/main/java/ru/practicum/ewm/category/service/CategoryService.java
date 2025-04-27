package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.dto.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto dto);

    CategoryDto update(int dtoId, UpdateCategoryDto dto);

    void delete(int dtoId);

    CategoryDto get(int id);

    List<CategoryDto> get(int from, int size);
}
