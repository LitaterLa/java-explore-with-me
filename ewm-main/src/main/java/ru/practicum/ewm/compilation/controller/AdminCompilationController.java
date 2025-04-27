package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class AdminCompilationController {
    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@Valid @RequestBody NewCompilationDto dto) {
        log.info("Админ сохраняет подборку событий {}", dto.getTitle());
        return service.save(dto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable Integer compId,
                                 @RequestBody @Valid UpdateCompilationDto dto) {
        log.info("Админ обновляет подборку событий {}", compId);
        return service.update(compId, dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer compId) {
        log.info("Админ удаляет подборку событий id={}", compId);
        service.delete(compId);
    }

}
