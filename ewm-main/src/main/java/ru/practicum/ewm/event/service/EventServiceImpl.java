package ru.practicum.ewm.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.StateAction;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entityParams.AdminGetListParams;
import ru.practicum.ewm.event.entityParams.PublicGetListParams;
import ru.practicum.ewm.event.location.Location;
import ru.practicum.ewm.event.location.LocationMapper;
import ru.practicum.ewm.event.location.LocationRepository;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.Sort;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.DataConflictException;
import ru.practicum.ewm.exception.IllegalAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.UpdateStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final EventMapper mapper;
    private final LocationMapper locationMapper;
    private final RequestMapper requestMapper;
    private final JPAQueryFactory queryFactory;
    private final StatsClient statsClient;


    @Override
    @Transactional
    public EventDto saveEvent(Integer userId, NewEventDto dto) {
        User initiator = validateUser(userId);

        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DataConflictException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }

        Location location = locationRepository.save(locationMapper.toLocationModel(dto.getLocation()));
        Event eventModel = mapper.toEventModel(dto);
        eventModel.setLocation(location);
        eventModel.setInitiator(initiator);
        Event saved = repository.save(eventModel);
        return mapper.toEventDto(saved);
    }

    @Override
    public List<EventDto> findAllByInitiatorId(Integer userId, int from, int size) {
        validateUser(userId);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Event> result = repository.findALlByInitiatorId(userId, pageRequest);
        return result.stream()
                .map(mapper::toEventDto)
                .toList();
    }

    @Override
    public EventDto findByEventId(Integer userId, Integer eventId) {
        Event event = validateEventByIdAndUserId(userId, eventId);
        return mapper.toEventDto(event);
    }


    @Override
    @Transactional
    public EventDto adminUpdateEvent(Integer eventId, UpdateEventAdminDto dto) {

        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (dto.getEventDate() != null && dto.getStateAction() == StateAction.PUBLISH_EVENT &&
                dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new DataConflictException("Дата начала события должна быть не ранее чем за час от текущего момента при публикации");
        }

        if (dto.getStateAction() == StateAction.PUBLISH_EVENT) {
            if (event.getState() != State.PENDING) {
                throw new DataConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
            }
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }

        if (dto.getStateAction() == StateAction.REJECT_EVENT) {
            if (event.getState() == State.PUBLISHED) {
                throw new DataConflictException("Событие можно отклонить, только если оно еще не опубликовано");
            }
            event.setState(State.CANCELED);
        }

        if (dto.getLocation() != null) {
            Location location = locationMapper.toLocationModel(dto.getLocation());
            Location savedLocation = locationRepository.save(location);
            event.setLocation(savedLocation);
        }

        return mapper.toEventDto(repository.save(mapper.updateEvent(dto, event)));
    }

    @Override
    public EventDto getById(Integer id) {
        Event event = repository.findById(id).orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (event.getPublishedOn() == null) {
            throw new NotFoundException("Событие id=" + id + " не найдено среди опубликованных событий");
        }

        EventDto eventDto = mapper.toEventDto(event);

        List<ViewStats> stats = statsClient.getStats(event.getCreatedOn(),
                LocalDateTime.now(),
                List.of("/events/" + id),
                true);

        int views = stats.isEmpty() ? 0 : stats.getFirst().getHits();
        eventDto.setViews(views + 1);

        return eventDto;
    }

    @Override
    public List<EventDto> getAdminInfo(AdminGetListParams params) {
        int page = params.getFrom() > params.getSize() ? params.getFrom() / params.getSize() : 0;
        PageRequest pageRequest = PageRequest.of(page, params.getSize());

        List<Event> eventsResult = searchDynamicallyByAdmin(params, pageRequest);

        return eventsResult.stream()
                .map(mapper::toEventDto)
                .toList();
    }


    @Override
    @Transactional
    public EventDto userUpdateEvent(Integer userId, Integer eventId, UpdateEventUserRequest dto) {
        validateUser(userId);
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getInitiator().getId() != userId) {
            throw new IllegalAccessException("Ошибка доступа к событию: не инициатор");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new DataConflictException("Можно изменить только события со статусом PENDING или CANCELLED");
        }

        if (dto.getStateAction() != null && dto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(State.CANCELED);
        } else if (dto.getStateAction() != null && dto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(State.PENDING);
        }

        Event saved = repository.save(mapper.updateEvent(dto, event));

        return mapper.toEventDto(saved);

    }


    @Override
    @Transactional
    public EventRequestStatusUpdateResult initiatorUpdateRequestStatus(int userId,
                                                                       int eventId,
                                                                       EventRequestStatusUpdateRequest updateRequest) {
        validateUser(userId);
        Event event = validateEventByIdAndUserId(userId, eventId);

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new DataConflictException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());

        for (ParticipationRequest request : requests) {
            if (!request.getStatus().equals(State.PENDING)) {
                throw new DataConflictException("Можно изменить заявки, находящиеся только в статусе PENDING");
            }
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        boolean autoConfirm = !event.getRequestModeration() || event.getParticipantLimit() == 0;
        int slots = event.getParticipantLimit() - event.getConfirmedRequests();

        State newState;
        if (autoConfirm || updateRequest.getStatus() == UpdateStatus.CONFIRMED) {
            newState = State.CONFIRMED;
        } else if (updateRequest.getStatus() == UpdateStatus.REJECTED) {
            newState = State.REJECTED;
        } else {
            throw new IllegalArgumentException("Неверно указан новый статус");
        }

        if (newState == State.CONFIRMED && !autoConfirm && requests.size() > slots) {
            for (ParticipationRequest request : requests) {
                request.setStatus(State.REJECTED);
            }
            result.setRejectedRequests(requests.stream()
                    .map(requestMapper::toDtoRequest)
                    .toList());
        } else {
            for (ParticipationRequest request : requests) {
                request.setStatus(newState);
            }
            if (newState == State.CONFIRMED) {
                event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
                repository.save(event);
                result.setConfirmedRequests(requests.stream()
                        .map(requestMapper::toDtoRequest)
                        .toList());
            } else {
                result.setRejectedRequests(requests.stream()
                        .map(requestMapper::toDtoRequest)
                        .toList());
            }
        }

        requestRepository.saveAll(requests);
        return result;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Integer userId, Integer eventId) {
        User initiator = validateUser(userId);
        Event event = validateEventByIdAndUserId(userId, eventId);

        if (initiator.getId() != event.getInitiator().getId()) {
            throw new BadRequestException("Доступ ограничен: пользователь " + userId + "не инициатор события " + eventId);
        }

        List<ParticipationRequest> requests = requestRepository.findByRequesterIdAndEventId(eventId);
        return requests.stream()
                .map(requestMapper::toDtoRequest)
                .toList();
    }


    @Override
    public List<EventShortDto> getPublicInfo(PublicGetListParams params) {
        if (CollectionUtils.isEmpty(params.getCategories()) ||
                params.getCategories().stream().allMatch(id -> id <= 0)) {
            throw new BadRequestException("Категории не указаны или содержат неверные ID");
        }

        if (params.getSize() == null || params.getSize() <= 0) {
            params.setSize(10);
        }

        int page = params.getSize() > 0 ? params.getFrom() / params.getSize() : 0;
        PageRequest pageRequest = PageRequest.of(page, params.getSize());

        List<Event> eventsResult = searchDynamicallyByUser(params, pageRequest);


        List<String> uris = eventsResult.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        LocalDateTime earliestCreatedOn = eventsResult.getFirst().getCreatedOn();

        List<ViewStats> stats = statsClient.getStats(earliestCreatedOn,
                LocalDateTime.now(),
                uris,
                true);

        Map<String, Integer> uriViews = stats.stream()
                .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

        return eventsResult.stream()
                .map(event -> {
                    EventShortDto dto = mapper.toEventShortDto(event);
                    String uriInner = "/events/" + event.getId();
                    Integer count = uriViews.getOrDefault(uriInner, 0);
                    dto.setViews(count + 1);
                    return dto;
                })
                .toList();
    }

    private List<Event> searchDynamicallyByUser(PublicGetListParams params, PageRequest pageRequest) {
        QEvent event = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();

        if (params.getText() != null && !params.getText().isBlank()) {
            builder.and(event.annotation.containsIgnoreCase(params.getText()))
                    .or(event.description.containsIgnoreCase(params.getText()));
        }

        if (params.getPaid() != null) {
            builder.and(event.paid.eq(params.getPaid()));
        }

        if (params.getOnlyAvailable()) {
            builder.and(event.participantLimit.gt(event.confirmedRequests));
        }

        if (params.getRangeStart() != null) {
            builder.and(event.eventDate.goe(params.getRangeStart()));
        }
        if (params.getRangeEnd() != null) {
            builder.and(event.eventDate.loe(params.getRangeEnd()));
        }
        if (params.getRangeStart() == null && params.getRangeEnd() == null) {
            builder.and(event.eventDate.goe(LocalDateTime.now().minusMinutes(10)));
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            builder.and(event.category.id.in(params.getCategories()));
        }

        OrderSpecifier<?> sortOrder = params.getSort().equals(Sort.VIEWS)
                ? event.views.desc()
                : event.eventDate.desc();

        return queryFactory
                .selectFrom(event)
                .where(builder)
                .orderBy(sortOrder)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();
    }

    private List<Event> searchDynamicallyByAdmin(AdminGetListParams params, PageRequest pageRequest) {
        QEvent event = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();

        if (!CollectionUtils.isEmpty(params.getUsers())) {
            builder.and(event.initiator.id.in(params.getUsers()));
        }

        if (!CollectionUtils.isEmpty(params.getCategories())) {
            builder.and(event.category.id.in(params.getCategories()));
        }

        if (!CollectionUtils.isEmpty(params.getStates())) {
            builder.and(event.state.in(params.getStates()));
        }

        if (params.getRangeStart() != null) {
            builder.and(event.eventDate.goe(params.getRangeStart()));
        }
        if (params.getRangeEnd() != null) {
            builder.and(event.eventDate.loe(params.getRangeEnd()));
        }
        if (params.getRangeStart() == null && params.getRangeEnd() == null) {
            builder.and(event.eventDate.goe(LocalDateTime.now()));
        }

        return repository.findAll(builder, pageRequest).toList();
    }


    private Event validateEventByIdAndUserId(Integer userId, Integer eventId) {
        return repository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    private User validateUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не был найден"));
    }

}
