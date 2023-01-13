package ru.practicum.explorewithme.events;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.EventFullDto;
import ru.practicum.explorewithme.events.dto.EventShortDto;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.participation.ParticipationRepository;
import ru.practicum.explorewithme.users.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class EventMapper {

    public static Event newEventDtoToEvent(EventDto eventDto, Category category, User initiator) {
        Event event = new Event();
        event.setAnnotation(eventDto.getAnnotation());
        event.setCategory(category);
        event.setConfirmedRequests(0);
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setLatitude(eventDto.getLocation().getLat());
        event.setLongitude(eventDto.getLocation().getLon());
        event.setPaid(eventDto.getPaid());
        event.setParticipantLimit(eventDto.getParticipantLimit());
        event.setRequestModeration(eventDto.getRequestModeration());
        event.setTitle(eventDto.getTitle());
        event.setInitiator(initiator);
        return event;
    }

    public static EventFullDto eventToEventFullDto(Event event, Integer confirmedRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EventFullDto.Location location = new EventFullDto.Location();
        location.setLat(event.getLatitude());
        location.setLon(event.getLongitude());
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setConfirmedRequests(confirmedRequest);
        eventFullDto.setCategory(event.getCategory());
        eventFullDto.setCreatedOn(event.getCreationOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(formatter));
        eventFullDto.setInitiator(event.getInitiator());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState().name());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());
        eventFullDto.setLocation(location);
        return eventFullDto;
    }

    public static EventShortDto eventToEventShortDto(Event event, Integer confirmedRequests) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EventShortDto eventDto = new EventShortDto();
        eventDto.setId(event.getId());
        eventDto.setEventDate(event.getEventDate().format(formatter));
        eventDto.setAnnotation(event.getAnnotation());
        eventDto.setCategory(event.getCategory());
        eventDto.setConfirmedRequests(confirmedRequests);
        eventDto.setInitiator(event.getInitiator());
        eventDto.setPaid(event.getPaid());
        eventDto.setViews(event.getViews());
        eventDto.setTitle(event.getTitle());
        return eventDto;
    }

    public static List<EventShortDto> eventsToEventsShortDto(List<Event> events, List<ParticipationRepository
            .CountParticipation> countConfirmedRequests) {
        List<EventShortDto> dtos = events.stream().map(event -> EventMapper
                .eventToEventShortDto(event, 0)).collect(Collectors.toList());
        if (!countConfirmedRequests.isEmpty()) {
            for (ParticipationRepository.CountParticipation count : countConfirmedRequests) {
                for (EventShortDto event : dtos) {
                    if (event.getId().equals(count.getId())) {
                        event.setConfirmedRequests(count.getCount());
                    }
                }
            }
        }
        return dtos;
    }

    public static List<EventFullDto> eventsToEventsFullDto(List<Event> events, List<ParticipationRepository
            .CountParticipation> countConfirmedRequests) {
        List<EventFullDto> dtos = events.stream().map(event -> EventMapper
                .eventToEventFullDto(event, 0)).collect(Collectors.toList());
        if (!countConfirmedRequests.isEmpty()) {
            for (ParticipationRepository.CountParticipation count : countConfirmedRequests) {
                for (EventFullDto event : dtos) {
                    if (event.getId().equals(count.getId())) {
                        event.setConfirmedRequests(count.getCount());
                    }
                }
            }
        }
        return dtos;
    }
}
