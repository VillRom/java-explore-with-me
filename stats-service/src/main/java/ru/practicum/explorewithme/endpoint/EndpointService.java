package ru.practicum.explorewithme.endpoint;

import ru.practicum.explorewithme.endpoint.dto.EndpointHitDto;
import ru.practicum.explorewithme.endpoint.dto.ViewsStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointService {

    void addHit(EndpointHitDto hit);

    List<ViewsStats> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
