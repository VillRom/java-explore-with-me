package ru.practicum.explorewithme.endpoint;

import ru.practicum.explorewithme.endpoint.dto.EndpointHitDto;
import ru.practicum.explorewithme.endpoint.dto.ViewsStats;

import java.util.List;
import java.util.Set;

public interface EndpointService {

    ViewsStats addHit(EndpointHitDto hit);

    List<ViewsStats> getHits(String start, String end, Set<String> uris, Boolean unique);
}
