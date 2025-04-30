package ru.practicum.ewm.event.location;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto toLocationDto(Location location);

    @Mapping(target = "id", ignore = true)
    Location toLocationModel(LocationDto dto);

}
