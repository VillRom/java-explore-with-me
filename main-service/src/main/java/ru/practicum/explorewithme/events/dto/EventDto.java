package ru.practicum.explorewithme.events.dto;

import lombok.Data;


/**
 * A DTO for the {@link ru.practicum.explorewithme.events.model.Event} entity
 */
@Data
public class EventDto {

    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String title;

    @Data
    public static class Location {

        private Double lon;

        private Double lat;
    }
}