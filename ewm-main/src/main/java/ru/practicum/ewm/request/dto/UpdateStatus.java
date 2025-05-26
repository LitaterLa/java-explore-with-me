package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UpdateStatus {
    CONFIRMED, PENDING, REJECTED;

    @JsonCreator
    UpdateStatus fromString(String str) {
        return UpdateStatus.valueOf(str.toUpperCase());
    }
}
