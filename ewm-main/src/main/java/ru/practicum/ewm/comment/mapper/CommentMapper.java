package ru.practicum.ewm.comment.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.user.mapper.UserMapper;

@Component
@Mapper(componentModel = "spring", uses = {EventMapper.class, UserMapper.class, CategoryMapper.class})
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "event", ignore = true)
    Comment toCommentModel(NewCommentDto dto);

    CommentDto toCommentDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment updateComment(UpdateCommentDto dto, @MappingTarget Comment comment);


}
