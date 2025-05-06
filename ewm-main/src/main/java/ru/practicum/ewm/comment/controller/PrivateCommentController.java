package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateCommentController {
    private final CommentService service;

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto saveComment(@Valid @RequestBody NewCommentDto dto,
                                  @PathVariable Integer userId,
                                  @PathVariable Integer eventId) {
        log.info("Пользователь {} публикует комментарий к событию {}", userId, eventId);
        return service.saveComment(dto, userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@Valid @RequestBody UpdateCommentDto dto,
                                    @PathVariable Integer userId,
                                    @PathVariable Integer eventId,
                                    @PathVariable Integer commentId) {
        log.info("Пользователь {} обновляет комментарий {} к событию {}", userId, commentId, eventId);
        return service.updateComment(dto, commentId, userId, eventId);
    }

    @DeleteMapping("/{userId}/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer userId,
                              @PathVariable Integer eventId,
                              @PathVariable Integer commentId) {
        log.info("Пользователь {} удаляет комментарий {} к событию {}", userId, commentId, eventId);
        service.deleteComment(userId, eventId, commentId);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentDto> getUsersComments(@PathVariable Integer userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение всех комментариев пользователя {}", userId);
        return service.getAllUsersComments(userId, from, size);
    }
}
