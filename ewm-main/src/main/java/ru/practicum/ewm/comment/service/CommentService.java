package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto saveComment(NewCommentDto dto, int userId, int eventId);

    CommentDto updateComment(UpdateCommentDto dto, int commentId, int userId, int eventId);

    CommentDto getComment(int commentId);

    List<CommentDto> getCommentsByEventId(int eventId, int from, int size);

    List<CommentDto> getAllUsersComments(int userId, int from, int size);

    void deleteComment(int userId, int eventId, int commentId);
}
