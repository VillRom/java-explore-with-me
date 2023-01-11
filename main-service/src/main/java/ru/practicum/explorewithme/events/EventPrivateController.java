package ru.practicum.explorewithme.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.EventFullDto;
import ru.practicum.explorewithme.events.dto.EventShortDto;
import ru.practicum.explorewithme.events.dto.UpdateEventRequest;
import ru.practicum.explorewithme.participation.dto.ParticipationDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class EventPrivateController {

    private final EventService eventService;

    @Autowired
    public EventPrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "1000") int size) {
        log.info("Get events by user Id {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("Update event by user Id {}", userId);
        return eventService.updateEvent(userId, updateEventRequest);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody EventDto eventDto) {
        log.info("Add event {}", eventDto);
        return eventService.addEvent(eventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getFullInfoAboutEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Get full info event by user Id {}", userId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto canceledEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Canceled event Id {}", eventId);
        return eventService.canceledEventByUser(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationDto> getInfoAboutRequestByEvent(@PathVariable Long userId, @PathVariable long eventId) {
        log.info("Get participation by user Id {} and event Id {}", userId, eventId);
        return eventService.getParticipantsInEventByUser(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationDto confirmRequestEventByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                                      @PathVariable Long reqId) {
        log.info("Confirmed request {} event Id {}", reqId, eventId);
        return eventService.confirmRequestInEventByUser(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationDto rejectRequestEventByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                                     @PathVariable Long reqId) {
        log.info("Reject request id {} event Id {}", reqId, eventId);
        return eventService.rejectRequestInEventByUser(userId, eventId, reqId);
    }
}
