package ru.practicum.ewm.event.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Sort {
    EVENT_DATE, VIEWS;

    @JsonCreator
    public static Sort fromString(String value) {
        return Sort.valueOf(value.toUpperCase());
    }
}
