package ru.practicum.explorewithme.events.dto;

import lombok.Data;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.users.model.User;

import java.time.LocalDateTime;
/**
 * A DTO for the {@link ru.practicum.explorewithme.events.model.Event} entity
 */

@Data
public class EventFullDto {

    private Long id;

    private String annotation;

    private Category category;

    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    private String description;

    private String eventDate;

    private User initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private String state;

    private String title;

    private Long views;

    @Data
    public static class Location {

        private Double lat;

        private Double lon;
    }
}
