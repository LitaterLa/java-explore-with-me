package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;


    @Override
    public CompilationDto save(NewCompilationDto dto) {
        Compilation compilation = mapper.toCompilationModel(dto);
        Set<Integer> eventsIds = dto.getEvents();
        Set<Event> events = getEventsFromIds(eventsIds);
        compilation.setEvents(events);

        return mapper.toCompilationDto(repository.save(compilation));
    }

    @Override
    public CompilationDto findById(int id) {
        Compilation compilation = repository.findById(id).orElseThrow(() -> new NotFoundException("Не найдена подборка id=" + id));
        return mapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> findAll(Boolean pinned, int from, int size) {
        int page = from >= size ? from / size : 0;
        PageRequest pageRequest = PageRequest.of(page, size);

        if (Boolean.TRUE.equals(pinned)) {
            Page<Compilation> allByPinned = repository.findAllByPinned(true, pageRequest);
            return allByPinned.stream()
                    .map(mapper::toCompilationDto)
                    .toList();
        }

        return repository.findAll(pageRequest).getContent().stream()
                .map(mapper::toCompilationDto)
                .toList();
    }

    @Override
    public CompilationDto update(int compId, UpdateCompilationDto dto) {
        Compilation compilation = repository.findById(compId).orElseThrow(() -> new NotFoundException("Не найдена подборка id=" + compId));

        Set<Integer> eventsIds = dto.getEvents();
        Set<Event> events = getEventsFromIds(eventsIds);

        Compilation updated = mapper.updateCompilation(dto, compilation);
        updated.setEvents(events);

        return mapper.toCompilationDto(repository.save(updated));
    }

    @Override
    public void delete(int compId) {
        repository.findById(compId).orElseThrow(() -> new NotFoundException("Не найдена подборка id=" + compId));
        repository.deleteById(compId);
    }

    private Set<Event> getEventsFromIds(Set<Integer> eventsIds) {
        if (eventsIds == null) {
            return Collections.EMPTY_SET;
        } else {
            return eventsIds.stream()
                    .map(e -> eventRepository.findById(e).orElseThrow(() -> new NotFoundException("Не найдено событие id=" + e)))
                    .collect(Collectors.toSet());
        }
    }


}
