package ru.practicum.explorewithme.endpoint;

import javafx.util.Pair;
import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.endpoint.dto.EndpointHitDto;
import ru.practicum.explorewithme.endpoint.dto.ViewsStats;
import ru.practicum.explorewithme.endpoint.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EndpointMapper {

    public static List<ViewsStats> endpointHitsToViewsStats(List<EndpointHit> hits) {
        return  hits.stream()
                .collect(Collectors.groupingBy(
                        hit -> new Pair<>(hit.getApp(), hit.getUri()), Collectors.counting()))
                .entrySet().stream().map(entry -> new ViewsStats(
                        entry.getKey().getKey(),
                        entry.getKey().getValue(),
                        entry.getValue().longValue()))
                .collect(Collectors.toList());
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
