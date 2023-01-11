package ru.practicum.explorewithme.adminrequest.events.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateEventRequest {

    @NotNull
    private Long eventId;

    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private Boolean paid;

    private Integer participantLimit;

    private String title;
}
