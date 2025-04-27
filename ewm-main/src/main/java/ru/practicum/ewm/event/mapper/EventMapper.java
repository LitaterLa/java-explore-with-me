package ru.practicum.ewm.event.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.location.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;

@Component
@Mapper(componentModel = "spring", uses = {LocationMapper.class, UserMapper.class, CategoryMapper.class})
public interface EventMapper {

    @Mapping(target = "category", source = "category.id")
    @Mapping(target = "initiator", source = "initiator.id")
    EventDto toEventDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "views", expression = "java(0)")
    @Mapping(target = "confirmedRequests", expression = "java(0)")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(ru.practicum.ewm.event.model.State.PENDING)")
    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "location", source = "location")
    Event toEventModel(NewEventDto dto);

    @Mapping(target = "category", source = "category.id")
    EventShortDto toEventShortDto(Event model);

    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "eventDate", source = "eventDate")
    @Mapping(target = "state", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event updateEvent(UpdateEventUserRequest request, @MappingTarget Event item);

    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "location", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event updateEvent(UpdateEventAdminDto request, @MappingTarget Event item);


}
