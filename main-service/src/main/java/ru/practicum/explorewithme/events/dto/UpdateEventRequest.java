package ru.practicum.explorewithme.events.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.explorewithme.events.model.Event} entity
 */
@Data
public class UpdateEventRequest implements Serializable {

    private Long eventId;

    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private Boolean paid;

    private Integer participantLimit;

    private String title;
}