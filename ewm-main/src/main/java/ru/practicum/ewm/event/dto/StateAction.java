package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StateAction {
    PUBLISH_EVENT, REJECT_EVENT, SEND_TO_REVIEW, CANCEL_REVIEW;

    @JsonCreator
    StateAction fromString(String str) {
        return StateAction.valueOf(str.toUpperCase());
    }

}
