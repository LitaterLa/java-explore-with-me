package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UpdateStatus {
    CONFIRMED, PENDING, REJECTED;

    //нужно ли прописывать такие методы или пройдет автосериализация сама?
    @JsonCreator
    UpdateStatus fromString(String str) {
        return UpdateStatus.valueOf(str.toUpperCase());
    }
}
