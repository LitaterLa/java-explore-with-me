package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entityParams.AdminGetListParams;
import ru.practicum.ewm.event.entityParams.PublicGetListParams;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    EventDto saveEvent(Integer userId, NewEventDto dto);

    List<EventDto> findAllByInitiatorId(Integer userId, int from, int size);

    EventDto findByEventId(Integer userId, Integer eventId);

    EventDto adminUpdateEvent(Integer eventId, UpdateEventAdminDto dto);

    EventDto getById(Integer id);

    List<EventDto> getAdminInfo(AdminGetListParams params);

    List<EventShortDto> getPublicInfo(PublicGetListParams params);

    EventDto userUpdateEvent(Integer userId, Integer eventId, UpdateEventUserRequest dto);

    EventRequestStatusUpdateResult initiatorUpdateRequestStatus(int userId, int eventId, EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Integer userId, Integer eventId);
}


