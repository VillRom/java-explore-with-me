package ru.practicum.explorewithme.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.adminRequest.events.dto.EventDto;
import ru.practicum.explorewithme.adminRequest.events.dto.UpdateEventRequest;
import ru.practicum.explorewithme.client.baseClient.BaseClient;

import java.util.Map;

@Service
public class PrivateClient extends BaseClient {

    private static final String API_PREFIX = "/users";

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
        return get("/" + userId + "/events?from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> updateEventByUserPrivate(Long userId, UpdateEventRequest updateEventRequest) {
        return patch("/" + userId + "/events", updateEventRequest);
    }

    public ResponseEntity<Object> addEventPrivate(Long userId, EventDto eventDto) {
        return post("/" + userId + "/events", eventDto);
    }

    public ResponseEntity<Object> getFullInfoAboutEventByUserPrivate(Long userId, Long eventId) {
        return get("/" + userId + "/events/" + eventId);
    }

    public ResponseEntity<Object> canceledEventByUserPrivate(Long userId, Long eventId) {
        return patch("/" + userId + "/events/" + eventId);
    }

    public ResponseEntity<Object> getInfoAboutRequestByEventPrivate(Long userId, Long eventId) {
        return get("/" + userId + "/events/" + eventId + "/requests");
    }

    public ResponseEntity<Object> confirmRequestEventByUserPrivate(Long userId, Long eventId, Long reqId) {
        return patch("/" + userId + "/events/" + eventId + "/requests/" + reqId + "/confirm");
    }

    public ResponseEntity<Object> rejectRequestEventByUserPrivate(Long userId, Long eventId, Long reqId) {
        return patch("/" + userId + "/events/" + eventId + "/requests/" + reqId + "/reject");
    }

    public ResponseEntity<Object> getRequestsByUserPrivate(Long userId) {
        return get("/" + userId + "/requests");
    }

    public ResponseEntity<Object> addRequestPrivate(Long userId, Long eventId) {
        Map<String, Object> parameters = Map.of(
                "eventId", eventId
        );
        return post("/" + userId + "/requests?eventId={eventId}", parameters);
    }

    public ResponseEntity<Object> cancelRequestPrivate(Long userId, Long requestId) {
        return patch("/" + userId + "/requests/" + requestId + "/cancel");
    }
}
