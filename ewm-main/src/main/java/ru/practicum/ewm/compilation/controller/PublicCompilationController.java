package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Slf4j
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable Integer compId) {
        log.info("Гость получает подборку событий id={}", compId);
        return service.findById(compId);
    }


    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false, defaultValue = "false") Boolean pinned,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Гость получает подборки событий по заданным параметрам");
        return service.findAll(pinned, from, size);
    }


}
