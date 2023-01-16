package ru.practicum.explorewithme.adminrequest.events.dto;

import lombok.Data;

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
