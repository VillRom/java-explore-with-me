package ru.practicum.explorewithme.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.adminrequest.events.dto.EventDto;
import ru.practicum.explorewithme.adminrequest.events.dto.UpdateEventRequest;
import ru.practicum.explorewithme.client.baseclient.BaseClient;

import java.util.Map;

@Service
public class PrivateClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    private static final String URL_EVENTS = "/events";

    private static final String URL_REQUESTS = "/requests";

    @Autowired
    public PrivateClient(@Value("${main-service.url}") String serviceUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getEventsByUserPrivate(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/" + userId + URL_EVENTS + "?from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> updateEventByUserPrivate(Long userId, UpdateEventRequest updateEventRequest) {
        return patch("/" + userId + URL_EVENTS, updateEventRequest);
    }

    public ResponseEntity<Object> addEventPrivate(Long userId, EventDto eventDto) {
        return post("/" + userId + URL_EVENTS, eventDto);
    }

    public ResponseEntity<Object> getFullInfoAboutEventByUserPrivate(Long userId, Long eventId) {
        return get("/" + userId + URL_EVENTS + "/" + eventId);
    }

    public ResponseEntity<Object> canceledEventByUserPrivate(Long userId, Long eventId) {
        return patch("/" + userId + URL_EVENTS + "/" + eventId);
    }

    public ResponseEntity<Object> getInfoAboutRequestByEventPrivate(Long userId, Long eventId) {
        return get("/" + userId + URL_EVENTS + "/" + eventId + URL_REQUESTS);
    }

    public ResponseEntity<Object> confirmRequestEventByUserPrivate(Long userId, Long eventId, Long reqId) {
        return patch("/" + userId + URL_EVENTS + "/" + eventId + URL_REQUESTS + "/" + reqId + "/confirm");
    }

    public ResponseEntity<Object> rejectRequestEventByUserPrivate(Long userId, Long eventId, Long reqId) {
        return patch("/" + userId + URL_EVENTS + "/" + eventId + URL_REQUESTS + "/" + reqId + "/reject");
    }

    public ResponseEntity<Object> getRequestsByUserPrivate(Long userId) {
        return get("/" + userId + URL_REQUESTS);
    }

    public ResponseEntity<Object> addRequestPrivate(Long userId, Long eventId) {
        Map<String, Object> parameters = Map.of(
                "eventId", eventId
        );
        return post("/" + userId + URL_REQUESTS + "?eventId={eventId}", parameters);
    }

    public ResponseEntity<Object> cancelRequestPrivate(Long userId, Long requestId) {
        return patch("/" + userId + URL_REQUESTS + "/" + requestId + "/cancel");
    }
}
