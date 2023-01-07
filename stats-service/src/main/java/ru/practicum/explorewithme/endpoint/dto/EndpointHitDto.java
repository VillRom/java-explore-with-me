package ru.practicum.explorewithme.endpoint.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.explorewithme.endpoint.model.EndpointHit} entity
 */
@Data
public class EndpointHitDto implements Serializable {

    private Long id;

    private String app;

    private String uri;

    private String ip;

    private String timestamp;
}