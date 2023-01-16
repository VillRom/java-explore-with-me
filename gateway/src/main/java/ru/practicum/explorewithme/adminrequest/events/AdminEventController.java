package ru.practicum.explorewithme.adminrequest.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.adminrequest.events.dto.EventDto;
import ru.practicum.explorewithme.client.AdminClient;

import java.util.Set;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
public class AdminEventController {

    private final AdminClient adminClient;

    @GetMapping
    public ResponseEntity<Object> searchEventsAdmin(@RequestParam(required = false) Set<Long> users,
                                                    @RequestParam(required = false) Set<String> states,
                                                    @RequestParam(required = false) Set<Long> categories,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "1000") int size) {
        log.info("Get request: search events by admin");
        return adminClient.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Object> updateEventAdmin(@PathVariable Long eventId, @RequestBody EventDto eventDto) {
        log.info("Update event by admin : {}", eventDto);
        return adminClient.updateEventAdmin(eventId, eventDto);
    }

    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<Object> publicationEventAdmin(@PathVariable Long eventId) {
        log.info("Publication event with event Id : {} , by admin", eventId);
        return adminClient.publicationEventAdmin(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public ResponseEntity<Object> dismissEventAdmin(@PathVariable Long eventId) {
        log.info("Reject event with Id : {} , by admin ", eventId);
        return adminClient.rejectEventAdmin(eventId);
    }
}
