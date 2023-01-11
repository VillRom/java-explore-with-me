package ru.practicum.explorewithme.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.adminrequest.categories.dto.CategoryDto;
import ru.practicum.explorewithme.adminrequest.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.adminrequest.events.dto.EventDto;
import ru.practicum.explorewithme.adminrequest.users.dto.UserDto;
import ru.practicum.explorewithme.client.baseclient.BaseClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminClient extends BaseClient {

    private static final String API_PREFIX = "/admin";

    private static final String URL_USERS = "/users";

    private static final String URL_EVENTS = "/events";

    private static final String URL_CATEGORIES = "/categories";

    private static final String URL_COMPILATIONS = "/compilations";

    @Autowired
    public AdminClient(@Value("${main-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> addUserAdmin(UserDto user) {
        return post(URL_USERS, user);
    }

    public ResponseEntity<Object> getUsersAdmin(Set<Long> ids, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "ids", ids.toArray(),
                "from", from,
                "size", size
        );
        return get(URL_USERS + "?ids={ids}&from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> deleteUserAdmin(Long userId) {
        return delete(URL_USERS + "/" + userId);
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
        StringBuilder urisBuilder = new StringBuilder(URL_EVENTS + "?");
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
        return put(URL_EVENTS + "/" + eventId, eventDto);
    }

    public ResponseEntity<Object> publicationEventAdmin(Long eventId) {
        return patch(URL_EVENTS + "/" + eventId + "/publish");
    }

    public ResponseEntity<Object> rejectEventAdmin(Long eventId) {
        return patch(URL_EVENTS + "/" + eventId + "/reject");
    }

    public ResponseEntity<Object> addCategoryAdmin(CategoryDto categoryDto) {
        return post(URL_CATEGORIES, categoryDto);
    }

    public ResponseEntity<Object> updateCategoryAdmin(CategoryDto categoryDto) {
        return patch(URL_CATEGORIES, categoryDto);
    }

    public ResponseEntity<Object> deleteCategoryAdmin(Long catId) {
        return delete(URL_CATEGORIES + "/" + catId);
    }

    public ResponseEntity<Object> addCompilationAdmin(NewCompilationDto compilationDto) {
        return post(URL_COMPILATIONS, compilationDto);
    }

    public ResponseEntity<Object> deleteCompilationAdmin(Long compId) {
        return delete(URL_COMPILATIONS + "/" + compId);
    }

    public ResponseEntity<Object> deleteEventFromCompilationAdmin(Long compId, Long eventId) {
        return delete(URL_COMPILATIONS + "/" + compId + URL_EVENTS + "/" + eventId);
    }

    public ResponseEntity<Object> addEventToCompilationAdmin(Long compId, Long eventId) {
        return patch(URL_COMPILATIONS + "/" + compId + URL_EVENTS + "/" + eventId);
    }

    public ResponseEntity<Object> unpinCompilationOnTheMainPageAdmin(Long compId) {
        return delete(URL_COMPILATIONS + "/" + compId + "/pin");
    }

    public ResponseEntity<Object> pinCompilationOnTheMainPageAdmin(Long compId) {
        return patch(URL_COMPILATIONS + "/" + compId + "/pin");
    }
}
