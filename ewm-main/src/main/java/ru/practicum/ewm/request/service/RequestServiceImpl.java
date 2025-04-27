package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;

    @Override
    public List<ParticipationRequestDto> getByUserId(int from, int size, int userId) {
        int page = from >= size ? from / size : 0;
        PageRequest pageRequest = PageRequest.of(page, size);
        findUserOrThrow(userId);
        Page<ParticipationRequest> result = repository.findAllByRequester(pageRequest, userId);
        return result.getContent()
                .stream()
                .map(mapper::toDtoRequest)
                .toList();
    }

    @Override
    public ParticipationRequestDto save(int userId, Integer eventId) {
        if (eventId == null) {
            throw new BadRequestException("В запросе отсутствует id события");
        }
        User user = findUserOrThrow(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        validateNoDuplicateRequest(user);
        validateNotEventInitiator(user, event);
        validateEventIsPublished(event);
        validateRequestLimitNotReached(event);
        ParticipationRequest request = ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .status(setStatusManually(event))
                .created(LocalDateTime.now())
                .build();


        repository.save(request);
        return mapper.toDtoRequest(request);
    }

    @Override
    public ParticipationRequestDto updateToCancel(int userId, int requestId) {
        findUserOrThrow(userId);
        ParticipationRequest request = repository.findById(requestId).orElseThrow(() -> new NotFoundException("не найдена заявка id=" + requestId));
        request.setStatus(State.CANCELED);
        return mapper.toDtoRequest(request);
    }

    private User findUserOrThrow(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private void validateNoDuplicateRequest(User user) {
        if (repository.existsByRequesterEmail(user.getEmail())) {
            throw new DataConflictException("нельзя добавить повторный запрос");
        }
    }

    private void validateNotEventInitiator(User user, Event event) {
        if (event.getInitiator().getId() == (user.getId())) {
            throw new DataConflictException("инициатор события не может добавить запрос на участие в своём событии");
        }
    }

    private void validateEventIsPublished(Event event) {
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataConflictException("нельзя участвовать в неопубликованном событии");
        }
    }

    private void validateRequestLimitNotReached(Event event) {
        long confirmed = repository.countByEventIdAndStatus(event.getId(), State.CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmed >= event.getParticipantLimit()) {
            throw new DataConflictException("достигнут лимит запросов на участие");
        }
    }

    private State setStatusManually(Event event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return State.CONFIRMED;
        }
        return State.PENDING;
    }


}
