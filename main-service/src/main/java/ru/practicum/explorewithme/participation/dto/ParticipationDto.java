package ru.practicum.explorewithme.participation.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link ru.practicum.explorewithme.participation.model.Participation} entity
 */
@Data
public class ParticipationDto implements Serializable {

    private Long id;

    private LocalDateTime created;

    private Long event;

    private Long requester;

    private RequestStatus status;
}