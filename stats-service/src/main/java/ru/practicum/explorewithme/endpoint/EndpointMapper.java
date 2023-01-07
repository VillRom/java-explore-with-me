package ru.practicum.explorewithme.endpoint;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.endpoint.dto.EndpointHitDto;
import ru.practicum.explorewithme.endpoint.dto.ViewsStats;
import ru.practicum.explorewithme.endpoint.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EndpointMapper {

    public static ViewsStats endpointHitToViewStats(EndpointHit hit, Long views) {
        ViewsStats viewsStats = new ViewsStats();
        viewsStats.setUri(hit.getUri());
        viewsStats.setApp(hit.getApp());
        viewsStats.setHits(views);
        return viewsStats;
    }

    public static EndpointHit endpointHitDtoToEndpointHit(EndpointHitDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EndpointHit hit = new EndpointHit();
        hit.setId(dto.getId());
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimestamp(LocalDateTime.parse(dto.getTimestamp(), formatter));
        return hit;
    }
}
