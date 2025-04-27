package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto save(NewCompilationDto dto);

    CompilationDto update(int compId, UpdateCompilationDto dto);

    void delete(int compId);

    CompilationDto findById(int id);

    List<CompilationDto> findAll(Boolean pinned, int from, int size);


}
