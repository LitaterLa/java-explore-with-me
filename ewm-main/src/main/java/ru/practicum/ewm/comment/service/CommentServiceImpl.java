package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.IllegalAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper mapper;
    @Value("${comment.editable.duration.hours}")
    private long editableDurationHours;


    @Override
    @Transactional
    public CommentDto saveComment(NewCommentDto dto, int userId, int eventId) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        if (repository.existsByUserIdAndEventId(userId, eventId)) {
            throw new BadRequestException("Комментарий от пользователя " + userId + " уже существует");
        }
        Comment comment = mapper.toCommentModel(dto);
        comment.setUser(user);
        comment.setEvent(event);
        repository.save(comment);
        return mapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentDto dto, int commentId, int userId, int eventId) {
        Comment comment = getCommentOrThrow(commentId);
        if (comment.getCreated().plusHours(editableDurationHours).isBefore(LocalDateTime.now())) {
            throw new BadRequestException("С момента публикации комментария прошло более "
                    + editableDurationHours + " часов, редактирование невозможно");

        }
        getUserOrThrow(userId);
        getEventOrThrow(eventId);
        Comment updated = mapper.updateComment(dto, comment);
        return mapper.toCommentDto(repository.save(updated));
    }

    @Override
    public CommentDto getComment(int commentId) {
        Comment comment = getCommentOrThrow(commentId);
        return mapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByEventId(int eventId, int from, int size) {
        getEventOrThrow(eventId);
        return getPaginatedComments(from, size,
                (pageRequest -> repository.findAllById(eventId, pageRequest)));
    }

    @Override
    public List<CommentDto> getAllUsersComments(int userId, int from, int size) {
        getUserOrThrow(userId);
        return getPaginatedComments(from, size,
                (pageRequest -> repository.findAllByUserId(userId, pageRequest)));
    }

    @Override
    @Transactional
    public void deleteComment(int userId, int eventId, int commentId) {
        User user = getUserOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);
        if (comment.getUser().getId() != user.getId() || comment.getEvent().getId() != eventId) {
            throw new IllegalAccessException("Ошибка при удалении комментария: неверный пользователь или событие");
        }
        repository.delete(comment);
    }

    private User getUserOrThrow(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + userId));
    }

    private Event getEventOrThrow(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие id=" + eventId));
    }

    private Comment getCommentOrThrow(int commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий id=" + commentId));
    }

    private List<CommentDto> getPaginatedComments(int from, int size,
                                                  Function<PageRequest, Page<Comment>> repositoryResult) {
        int page = from > size ? from / size : 0;
        PageRequest pageRequest = PageRequest.of(page, size);
        return repositoryResult.apply(pageRequest)
                .getContent()
                .stream()
                .map(mapper::toCommentDto)
                .toList();

    }
}
