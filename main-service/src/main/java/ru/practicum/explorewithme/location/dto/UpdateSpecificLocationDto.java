package ru.practicum.explorewithme.location.dto;

import lombok.Data;
import ru.practicum.explorewithme.events.model.Event;

import java.util.List;

/**
 * A DTO for the {@link ru.practicum.explorewithme.location.model.SpecificLocation} entity
 */
@Data
public class UpdateSpecificLocationDto {

    private String name;

    private Double longitude;

    private Double latitude;

    private Integer radius;

    private List<Event> events;
}