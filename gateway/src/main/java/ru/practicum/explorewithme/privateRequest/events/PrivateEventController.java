package ru.practicum.explorewithme.privateRequest.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.adminRequest.events.dto.EventDto;
import ru.practicum.explorewithme.adminRequest.events.dto.UpdateEventRequest;
import ru.practicum.explorewithme.client.PrivateClient;

import javax.validation.Valid;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController {

    private final PrivateClient privateClient;

    @GetMapping("/{userId}/events")
    public ResponseEntity<Object> getEventsByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "1000") int size) {
        log.info("Get events by user Id {}", userId);
        return privateClient.getEventsByUserPrivate(userId, from, size);
    }

    @PatchMapping("/{userId}/events")
    public ResponseEntity<Object> updateEventByUser(@PathVariable Long userId,
                                                    @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        log.info("Update event by user Id {}", userId);
        return privateClient.updateEventByUserPrivate(userId, updateEventRequest);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<Object> addEvent(@PathVariable Long userId, @RequestBody EventDto eventDto) {
        log.info("Add event {}", eventDto);
        return privateClient.addEventPrivate(userId, eventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> getFullInfoAboutEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get full info event by user Id {}", userId);
        return privateClient.getFullInfoAboutEventByUserPrivate(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> canceledEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Canceled event Id {}", eventId);
        return privateClient.canceledEventByUserPrivate(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> getInfoAboutRequestByEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get participation by user Id {} and event Id {}", userId, eventId);
        return privateClient.getInfoAboutRequestByEventPrivate(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ResponseEntity<Object> confirmRequestEventByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                                            @PathVariable Long reqId) {
        log.info("Confirmed request {} event Id {}", reqId, eventId);
        return privateClient.confirmRequestEventByUserPrivate(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ResponseEntity<Object> rejectRequestEventByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                                           @PathVariable Long reqId) {
        log.info("Reject request id {} event Id {}", reqId, eventId);
        return privateClient.rejectRequestEventByUserPrivate(userId, eventId, reqId);
    }
}
