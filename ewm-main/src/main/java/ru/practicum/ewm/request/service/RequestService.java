package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getByUserId(int from, int size, int userId);

    ParticipationRequestDto save(int userId, Integer eventId);

    ParticipationRequestDto updateToCancel(int userId, int requestId);
}
