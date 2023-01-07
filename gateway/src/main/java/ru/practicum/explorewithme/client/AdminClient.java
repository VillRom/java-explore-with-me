package ru.practicum.explorewithme.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.adminRequest.categories.dto.CategoryDto;
import ru.practicum.explorewithme.adminRequest.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.adminRequest.events.dto.EventDto;
import ru.practicum.explorewithme.adminRequest.users.dto.UserDto;
import ru.practicum.explorewithme.client.baseClient.BaseClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminClient extends BaseClient {

    private static final String API_PREFIX = "/admin";

    @Autowired
    public AdminClient(@Value("${main-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> addUserAdmin(UserDto user) {
        return post("/users", user);
    }

    public ResponseEntity<Object> getUsersAdmin(Set<Long> ids, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "ids", ids.toArray(),
                "from", from,
                "size", size
        );
        return get("/users?ids={ids}&from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> deleteUserAdmin(Long userId) {
        return delete("/users/" + userId);
    }

    public ResponseEntity<Object> getEventsAdmin(Set<Long> users, Set<String> states, Set<Long> categories,
                                                 String rangeStart, String rangeEnd, int from, int size) {
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
        maps.add(users);
        maps.add(states);
        maps.add(categories);
        maps.add(rangeStart);
        maps.add(rangeEnd);
        maps.add(from);
        maps.add(size);
        List<String> strings = List.of("users", "states", "categories", "rangeStart",
                "rangeEnd", "from", "size");
        String uris;
        Map<String, Object> map = new HashMap<>();
        StringBuilder urisBuilder = new StringBuilder("/events?");
        for (int i = 0; i < maps.size(); i++) {
            if (maps.get(i) != null) {
                if (i == 0) {
                    map.put(strings.get(i), users.toArray());
                    urisBuilder.append(strings.get(i)).append("={").append(strings.get(i)).append("}&");
                } else if (i == 1) {
                    map.put(strings.get(i), states.toArray());
                    urisBuilder.append(strings.get(i)).append("={").append(strings.get(i)).append("}&");
                } else if (i == 2) {
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

    public ResponseEntity<Object> updateEventAdmin(Long eventId, EventDto eventDto) {
        return put("/events/" + eventId, eventDto);
    }

    public ResponseEntity<Object> publicationEventAdmin(Long eventId) {
        return patch("/events/" + eventId + "/publish");
    }

    public ResponseEntity<Object> rejectEventAdmin(Long eventId) {
        return patch("/events/" + eventId + "/reject");
    }

    public ResponseEntity<Object> addCategoryAdmin(CategoryDto categoryDto) {
        return post("/categories", categoryDto);
    }

    public ResponseEntity<Object> updateCategoryAdmin(CategoryDto categoryDto) {
        return patch("/categories", categoryDto);
    }

    public ResponseEntity<Object> deleteCategoryAdmin(Long catId) {
        return delete("/categories/" + catId);
    }

    public ResponseEntity<Object> addCompilationAdmin(NewCompilationDto compilationDto) {
        return post("/compilations", compilationDto);
    }

    public ResponseEntity<Object> deleteCompilationAdmin(Long compId) {
        return delete("/compilations/" + compId);
    }

    public ResponseEntity<Object> deleteEventFromCompilationAdmin(Long compId, Long eventId) {
        return delete("/compilations/" + compId + "/events/" + eventId);
    }

    public ResponseEntity<Object> addEventToCompilationAdmin(Long compId, Long eventId) {
        return patch("/compilations/" + compId + "/events/" + eventId);
    }

    public ResponseEntity<Object> unpinCompilationOnTheMainPageAdmin(Long compId) {
        return delete("/compilations/" + compId + "/pin");
    }

    public ResponseEntity<Object> pinCompilationOnTheMainPageAdmin(Long compId) {
        return patch("/compilations/" + compId + "/pin");
    }
}
