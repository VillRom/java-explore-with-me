package ru.practicum.explorewithme.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.EventFullDto;
import ru.practicum.explorewithme.events.dto.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/admin/events")
public class EventAdminController {

    private final EventService eventService;

    @Autowired
    public EventAdminController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> searchEventsAdmin(@RequestParam Set<Long> users,
                                                @RequestParam(required = false) Set<String> states,
                                                @RequestParam Set<Long> categories,
                                                @RequestParam(required = false) @DateTimeFormat(pattern =
                                                        "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern =
                                                        "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "1000") int size) {
        log.info("Get request: search events by admin");
        List<EventState> statesReform;
        if (states != null) {
            statesReform = states.stream().map(EventState::valueOf).collect(Collectors.toList());
        } else {
            statesReform = null;
        }
        return eventService.searchEventsByAdmin(users, statesReform, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Long eventId, @RequestBody EventDto eventDto) {
        log.info("Update event by admin : {}", eventDto);
        return eventService.updateEventByAdmin(eventId, eventDto);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publicationEventAdmin(@PathVariable Long eventId) {
        log.info("Publication event with event Id : {} , by admin", eventId);
        return eventService.publicationEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto dismissEventAdmin(@PathVariable Long eventId) {
        log.info("Reject event with Id : {} , by admin ", eventId);
        return eventService.rejectEvent(eventId);
    }
}
