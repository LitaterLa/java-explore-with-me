package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndpointHit;
import ru.practicum.StatsClient;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.entityParams.PublicGetListParams;
import ru.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService service;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> get(@Valid PublicGetListParams params, HttpServletRequest request) {
        log.info("Получение информации о событиях из контроллера PublicEventController");
        log.info("Гость: client ip: {}", request.getRemoteAddr());
        log.info("Гость: endpoint path: {}", request.getRequestURI());

        statsClient.sendHit(EndpointHit.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .created(LocalDateTime.now())
                .uri(request.getRequestURI())
                .build());
        return service.getPublicInfo(params);
    }

    @GetMapping("/{id}")
    public EventDto get(@PathVariable Integer id, HttpServletRequest request) {
        log.info("Получение информации о событии id={} из контроллера PublicEventController", id);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());

        statsClient.sendHit(EndpointHit.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .created(LocalDateTime.now())
                .uri(request.getRequestURI())
                .build());
        return service.getById(id);
    }
}
