package ru.practicum.explorewithme.endpoint.dto;

import lombok.Data;
import ru.practicum.explorewithme.endpoint.model.EndpointHit;

import java.io.Serializable;

/**
 * A DTO for the {@link EndpointHit} entity
 */
@Data
public class ViewsStats implements Serializable {

    private String app;

    private String uri;

    private Long hits;

    public ViewsStats(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}