package ru.practicum.ewm.event.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum State {
    PENDING, PUBLISHED, CANCELED, REJECTED, CONFIRMED;

    @JsonCreator
    public static State fromString(String value) {
        return State.valueOf(value.toUpperCase());
    }
}

