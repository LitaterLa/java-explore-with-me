package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/users")
public class PrivateEventController {
    private final EventService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    public EventDto save(@PathVariable Integer userId,
                         @RequestBody @Valid NewEventDto dto) {
        log.info("Пользователь сохраняет событие {}", dto.getTitle());
        return service.saveEvent(userId, dto);
    }

    @GetMapping("/{userId}/events")
    public List<EventDto> getManyByUserId(@PathVariable Integer userId,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Пользователь {} получает список событий", userId);
        return service.findAllByInitiatorId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getOneByUserId(@PathVariable Integer userId,
                                   @PathVariable Integer eventId) {
        log.info("Пользователь {} получает событие {}", userId, eventId);
        return service.findByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto update(@PathVariable Integer userId,
                           @PathVariable Integer eventId,
                           @RequestBody @Valid UpdateEventUserRequest dto) {
        log.info("Пользователь {} обновляет событие {}", userId, eventId);
        return service.userUpdateEvent(userId, eventId, dto);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@PathVariable Integer userId,
                                                        @PathVariable Integer eventId,
                                                        @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Пользователь {} обновляет статус заявки на событие {}", userId, eventId);
        return service.initiatorUpdateRequestStatus(userId, eventId, request);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(@PathVariable Integer userId,
                                                                       @PathVariable Integer eventId) {
        log.info("Пользователь id={} получает запросы на событие id={}", userId, eventId);
        return service.getRequestsByUserIdAndEventId(userId, eventId);

    }
}
