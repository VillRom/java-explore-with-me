package ru.practicum.explorewithme.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.endpoint.dto.EndpointHitDto;
import ru.practicum.explorewithme.endpoint.dto.ViewsStats;
import ru.practicum.explorewithme.endpoint.model.EndpointHit;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndpointServiceImpl implements EndpointService {

    private final EndpointRepository endpointRepository;

    @Override
    @Transactional
    public void addHit(EndpointHitDto hit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        hit.setTimestamp(LocalDateTime.now().format(formatter));
        endpointRepository.save(EndpointMapper.endpointHitDtoToEndpointHit(hit));
    }

    @Override
    public List<ViewsStats> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<String> urisList = new ArrayList<>();
        for (String uri : uris) {
            uri = uri.replace("[", "").replace("]", "");
            urisList.add(URLDecoder.decode(uri, StandardCharsets.UTF_8));
        }
        List<EndpointHit> hits = endpointRepository.getEndpointHitsWithTimeIntervalAndUris(start, end, urisList);
        if (unique) {
            hits = hits.stream()
                    .sorted(Comparator.comparing(EndpointHit::getIp))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if (hits.isEmpty()) {
            ViewsStats nullableViews = new ViewsStats("unavailable", "unavailable", 0L);
            return List.of(nullableViews);
        } else {
            return EndpointMapper.endpointHitsToViewsStats(hits);
        }
    }
}
