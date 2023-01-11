package ru.practicum.explorewithme.publicrequests.dto;

import java.util.Optional;

public enum EventSort {

    EVENT_DATE,

    VIEWS;

    public static Optional<EventSort> from(String stringState) {
        for (EventSort state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return java.util.Optional.of(state);
            }
        }
        return java.util.Optional.empty();
    }
}
