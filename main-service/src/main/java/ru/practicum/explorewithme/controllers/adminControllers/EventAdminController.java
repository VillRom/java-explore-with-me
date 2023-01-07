package ru.practicum.explorewithme.controllers.adminControllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.EventService;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.EventFullDto;

import java.util.List;
import java.util.Set;

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
                                                @RequestParam(required = false) String rangeStart,
                                                @RequestParam(required = false) String rangeEnd,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "1000") int size) {
        log.info("Get request: search events by admin");
        return eventService.searchEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
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
