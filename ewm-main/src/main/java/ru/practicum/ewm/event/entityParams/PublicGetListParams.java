package ru.practicum.ewm.event.entityParams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.event.model.Sort;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PublicGetListParams {
    String text;
    List<Integer> categories;
    Boolean paid;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeEnd;
    @Builder.Default
    Boolean onlyAvailable = false;
    @Builder.Default
    Sort sort = Sort.EVENT_DATE;
    @Builder.Default
    Integer from = 0;
    @Builder.Default
    Integer size = 10;
}
