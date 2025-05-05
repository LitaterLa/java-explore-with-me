package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService service;

    @GetMapping("/comments/{id}")
    public CommentDto getById(@PathVariable Integer id) {
        log.info("Получение комментария {}", id);
        return service.getComment(id);
    }

    @GetMapping("/{eventId}/comments")
    public List<CommentDto> getCommentsByEventId(@PathVariable Integer eventId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение комментариев события {}", eventId);
        return service.getCommentsByEventId(eventId, from, size);
    }


}
