package ru.practicum.explorewithme.events;

import ru.practicum.explorewithme.events.dto.EventFullDto;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.EventShortDto;
import ru.practicum.explorewithme.events.dto.UpdateEventRequest;
import ru.practicum.explorewithme.participation.dto.ParticipationDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    //Получение списка событий с возможностью фильтрации
    List<EventShortDto> getPublishedEventsWithFilter(String text, Set<Long> categoriesId, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable, String sort, int from, int size,
                                                     HttpServletRequest request);

    //Получение опубликованного события по id
    EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request);

    //Добавление нового события
    EventFullDto addEvent(EventDto eventDto, Long userId);

    // Получение событий, добавленных текущим пользователем
    List<EventShortDto> getEventsByUserId(Long userId, int from, int size);

    //Изменение события добавленного текущим пользователем
    EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest);

    //Получение полной информации о событии добавленном текущим пользователем
    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    //Отмена события добавленного текущим пользователем
    EventFullDto canceledEventByUser(Long userId, Long eventId);

    //Получение информации о запросах на участие в событии текущего пользователя
    List<ParticipationDto> getParticipantsInEventByUser(Long userId, Long eventId);

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    ParticipationDto confirmRequestInEventByUser(Long userId, Long eventId, Long reqId);

    //Отклонение чужой заявки на участие в событии текущего пользователя
    ParticipationDto rejectRequestInEventByUser(Long userId, Long eventId, Long reqId);

    //Получение всех событий администратором
    List<EventFullDto> searchEventsByAdmin(Set<Long> usersId, Set<String> states, Set<Long> categoriesId,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    //Изменение события администротором
    EventFullDto updateEventByAdmin(Long eventId, EventDto eventDto);

    //Публикация события администротором
    EventFullDto publicationEvent(Long eventId);

    //отклонение события администротором
    EventFullDto rejectEvent(Long eventId);
}
