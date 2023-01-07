package ru.practicum.explorewithme.events.dto;

import lombok.Data;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.users.model.User;


/**
 * A DTO for the {@link ru.practicum.explorewithme.events.model.Event} entity
 */
@Data
public class EventShortDto {

    private Long id;

    private String annotation;

    private Integer confirmedRequests;

    private Category category;

    private String eventDate;

    private User initiator;

    private Boolean paid;

    private String title;

    private Long views;
}