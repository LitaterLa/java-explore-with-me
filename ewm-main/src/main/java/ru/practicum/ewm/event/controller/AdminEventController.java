package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.entityParams.AdminGetListParams;
import ru.practicum.ewm.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventController {
    private final EventService service;

    @GetMapping
    public List<EventDto> get(@Valid AdminGetListParams params) {
        log.info("Администратор получает список событий по заданным параметрам");
        return service.getAdminInfo(params);
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable Integer eventId, @RequestBody @Valid UpdateEventAdminDto dto) {
        log.info("Администратор обновляет событие с id={}", eventId);
        return service.adminUpdateEvent(eventId, dto);
    }
}
