package ru.practicum.explorewithme.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.client.baseClient.BaseClient;
import ru.practicum.explorewithme.exceptions.RequestException;
import ru.practicum.explorewithme.publicRequests.dto.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PublicClient extends BaseClient {

    @Autowired
    public PublicClient(@Value("${main-service.url}") String serviceUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getAllCategoriesPublic(int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/categories?from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> getCategoryByIdPublic(Long catId) {
        return get("/categories/" + catId);
    }

    public ResponseEntity<Object> getCompilationsPublic(Boolean pinned, int from, int size) {
        Map<String, Object> parameters;
        if (pinned == null) {
            parameters = Map.of(
                    "from", from,
                    "size", size
            );
            return get("/compilations?from={from}&size={size}", parameters);
        } else {
            parameters = Map.of(
                    "pinned", pinned,
                    "from", from,
                    "size", size
            );
            return get("/compilations?pinned={pinned}&from={from}&size={size}", parameters);
        }
    }

    public ResponseEntity<Object> getCompilationByIdPublic(Long compId) {
        return get("/compilations/" + compId);
    }

    public ResponseEntity<Object> getEventsWithFilterPublic(String text, Set<Long> categories, Boolean paid,
                                                            String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                            String sort, int from, int size) {
        if (sort != null) {
            EventSort.from(sort).orElseThrow(() -> new RequestException("Unknown state: " + sort));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now().format(formatter);
            rangeEnd = LocalDateTime.now().plusYears(100).format(formatter);
        } else if (rangeStart != null && rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100).format(formatter);
        } else if (rangeStart == null) {
            rangeStart = LocalDateTime.now().format(formatter);
        }
        List<Object> maps = new LinkedList<>();
        maps.add(text);
        maps.add(categories);
        maps.add(paid);
        maps.add(rangeStart);
        maps.add(rangeEnd);
        maps.add(onlyAvailable);
        maps.add(sort);
        maps.add(from);
        maps.add(size);
        List<String> strings = List.of("text", "categories", "paid", "rangeStart",
                "rangeEnd", "onlyAvailable", "sort", "from", "size");
        String uris;
        Map<String, Object> map = new HashMap<>();
        StringBuilder urisBuilder = new StringBuilder("/events?");
        for (int i = 0; i < maps.size(); i++) {
            if (maps.get(i) != null) {
                if (i == 1) {
                    map.put(strings.get(i), categories.toArray());
                    urisBuilder.append(strings.get(i)).append("={").append(strings.get(i)).append("}&");
                } else {
                    map.put(strings.get(i), maps.get(i));
                    urisBuilder.append(strings.get(i)).append("={").append(strings.get(i)).append("}&");
                }
            }
        }
        uris = urisBuilder.substring(0, urisBuilder.length() - 1);
        return get(uris, map);
    }

    public ResponseEntity<Object> getFullInfoAboutEventByIdPublic(Long id, HttpServletRequest request) {
        Map<String, Object> parameters = Map.of(
                "request", request
        );
        return get("/events/" + id + "?request={request}", parameters);
    }
}
