package ru.practicum.explorewithme.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.endpoint.dto.EndpointHitDto;
import ru.practicum.explorewithme.endpoint.dto.ViewsStats;
import ru.practicum.explorewithme.endpoint.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndpointServiceImpl implements EndpointService {

    private final EndpointRepository endpointRepository;

    @Override
    @Transactional
    public ViewsStats addHit(EndpointHitDto hit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        hit.setTimestamp(LocalDateTime.now().format(formatter));
        return EndpointMapper.endpointHitToViewStats(endpointRepository.save(EndpointMapper
                .endpointHitDtoToEndpointHit(hit)), endpointRepository
                .countByAppIgnoreCaseAndUriIgnoreCase(hit.getApp(), hit.getUri()));
    }

    @Override
    public List<ViewsStats> getHits(String start, String end, Set<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        List<EndpointHit> hits;
        List<EndpointHit> list;
        if (uris == null && !unique) {
            hits = endpointRepository.getEndpointHitsByTimestampIsAfterAndTimestampIsBefore(startTime, endTime);
        } else if (uris == null) {
            hits = endpointRepository.getDistinctByTimestampIsAfterAndTimestampIsBefore(startTime, endTime);
        } else if (!unique) {
            hits = endpointRepository.getEndpointHitsByTimestampIsAfterAndTimestampIsBeforeAndUriIsIn(startTime,
                    endTime, uris);
            list = endpointRepository.findAll();
            System.out.println(list);
        } else {
            hits = endpointRepository.getDistinctByTimestampIsAfterAndTimestampIsBeforeAndUriIsIn(startTime,
                    endTime, uris);
        }
        List<ViewsStats> stats = new ArrayList<>();
        for (EndpointHit hit : hits) {
            stats.add(EndpointMapper.endpointHitToViewStats(hit, endpointRepository
                    .countByAppIgnoreCaseAndUriIgnoreCase(hit.getApp(), hit.getUri())));
        }
        return stats;
    }
}
