package ru.practicum.explorewithme.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.categories.CategoryRepository;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.EventFullDto;
import ru.practicum.explorewithme.events.dto.EventShortDto;
import ru.practicum.explorewithme.events.dto.UpdateEventRequest;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.exception.EventsException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.RequestException;
import ru.practicum.explorewithme.participation.ParticipationMapper;
import ru.practicum.explorewithme.participation.ParticipationRepository;
import ru.practicum.explorewithme.participation.dto.ParticipationDto;
import ru.practicum.explorewithme.participation.model.Participation;
import ru.practicum.explorewithme.statsclient.StatsClient;
import ru.practicum.explorewithme.statsclient.endpoint.EndpointHit;
import ru.practicum.explorewithme.users.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;

    private final ParticipationRepository participationRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getPublishedEventsWithFilter(String text, Set<Long> categoriesId, Boolean paid,
                                                            String rangeStart, String  rangeEnd, Boolean onlyAvailable,
                                                            String sort, int from, int size, HttpServletRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart == null && rangeEnd == null) {
            start = LocalDateTime.now();
            end = LocalDateTime.now().plusYears(100);
        } else if (rangeStart != null && rangeEnd == null) {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.now().plusYears(100);
        } else if (rangeStart == null) {
            start = LocalDateTime.now();
            end = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        Page<Event> eventsWithoutSort;
        if (sort != null && sort.equals("EVENT_DATE")) {
            eventsWithoutSort = eventRepository.getEventsWithSortEventDate(categoriesId,
                    paid, start, end, text, text, PageRequest.of(from, size));
        } else if (sort != null && sort.equals("VIEWS")) {
            eventsWithoutSort = eventRepository.getEventsWithSortViews(categoriesId,
                    paid, start, end, text, text, PageRequest.of(from, size));
            
        } else {
            eventsWithoutSort = eventRepository.findAll(PageRequest.of(from, size));
        }
        List<EventShortDto> eventsShortDto = new ArrayList<>();
        Long view = getViewsByEvent(request.getRequestURI(), request.getRemoteAddr());
        for (Event event : Objects.requireNonNull(eventsWithoutSort)) {
            event.setViews(view);
            eventsShortDto.add(EventMapper.eventToEventShortDto(event, participationRepository
                    .countByEvent_IdAndStatusContaining(event.getId(), "CONFIRMED")));
        }
        return eventsShortDto;
    }

    @Override
    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        Event event = eventRepository.findEventByIdAndStateContainsIgnoreCase(eventId, "PUBLISHED");
        event.setViews(getViewsByEvent(request.getRequestURI(), request.getRemoteAddr()));
        return EventMapper.eventToEventFullDto(event,
                participationRepository.countByEvent_IdAndStatusContaining(eventId, "CONFIRMED"));
    }

    @Override
    @Transactional
    public EventFullDto addEvent(EventDto eventDto, Long userId) {
        if (eventDto.getAnnotation() == null || eventDto.getCategory() == null || eventDto.getDescription() == null ||
                eventDto.getEventDate() == null || eventDto.getLocation() == null || eventDto.getTitle() == null) {
            throw new RequestException("Пустыми могут быть только поля paid, participantLimit, requestModeration");
        }
        if (!categoryRepository.existsById(eventDto.getCategory())) {
            throw new RequestException("Категория с id-" + eventDto.getCategory() + " не найдена");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        if (LocalDateTime.parse(eventDto.getEventDate(), formatter).
                isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventsException("Время начала события не может быть раньше, чем через 2 часа");
        }
        Event event = EventMapper.newEventDtoToEvent(eventDto, categoryRepository
                .getReferenceById(eventDto.getCategory()), userRepository.getReferenceById(userId));
        event.setCreationOn(LocalDateTime.now());
        event.setState("PENDING");
        return EventMapper.eventToEventFullDto(eventRepository.save(event), 0);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        Page<Event> events = eventRepository.findAllByInitiator_Id(userId, PageRequest.of(from, size));
        List<EventShortDto> eventsShortDto = new ArrayList<>();
        for (Event event : events) {
            eventsShortDto.add(EventMapper.eventToEventShortDto(event, participationRepository
                    .countByEvent_IdAndStatusContaining(event.getId(), "CONFIRMED")));
        }
        return eventsShortDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        if (!eventRepository.existsById(updateEventRequest.getEventId())) {
            throw new NotFoundException("Событие с id-" + updateEventRequest.getEventId() + " не найдено");
        }
        if (eventRepository.getReferenceById(updateEventRequest.getEventId()).getState().equals("PUBLISHED")) {
            throw new EventsException("Событие нельзя изменить, т.к. оно уже опубликованно");
        }
        if (LocalDateTime.parse(updateEventRequest.getEventDate(), formatter).
                isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventsException("Время начала события не может быть раньше, чем через 2 часа");
        }
        Event updateEvent = eventRepository.getReferenceById(updateEventRequest.getEventId());
        if (!updateEventRequest.getAnnotation().equals(updateEvent.getAnnotation()) && updateEventRequest
                .getAnnotation() != null) {
            updateEvent.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (!updateEventRequest.getCategory().equals(updateEvent.getCategory().getId()) && updateEventRequest
                .getCategory() != null) {
            updateEvent.setCategory(categoryRepository.getReferenceById(updateEventRequest.getCategory()));
        }
        if (!updateEventRequest.getDescription().equals(updateEvent.getDescription()) && updateEventRequest
                .getDescription() != null) {
            updateEvent.setDescription(updateEventRequest.getDescription());
        }
        if (!updateEventRequest.getEventDate().equals(updateEvent.getEventDate().format(formatter))
                && updateEventRequest.getEventDate() != null) {
            updateEvent.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), formatter));
        }
        if (updateEventRequest.getPaid() != (updateEvent.getPaid()) && updateEventRequest
                .getPaid() != null) {
            updateEvent.setPaid(updateEventRequest.getPaid());
        }
        if (!updateEventRequest.getParticipantLimit().equals(updateEvent.getParticipantLimit()) && updateEventRequest
                .getParticipantLimit() != null) {
            updateEvent.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (!updateEventRequest.getTitle().equals(updateEvent.getTitle()) && updateEventRequest
                .getTitle() != null) {
            updateEvent.setTitle(updateEventRequest.getTitle());
        }
        if (updateEvent.getState().equals("CANCELED")) {
            updateEvent.setState("PENDING");
        }
        return EventMapper.eventToEventFullDto(eventRepository.save(updateEvent),
                participationRepository.countByEvent_IdAndStatusContaining(updateEvent.getId(), "CONFIRMED"));
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        return EventMapper.eventToEventFullDto(eventRepository.findByIdAndInitiator_Id(eventId, userId),
                participationRepository.countByEvent_IdAndStatusContaining(eventId, "CONFIRMED"));
    }

    @Override
    @Transactional
    public EventFullDto canceledEventByUser(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        if (!eventRepository.getReferenceById(eventId).getState().equals("PENDING")) {
            throw new EventsException("Событие нельзя отменить, т.к. его статус - "
                    + eventRepository.getReferenceById(eventId).getState());
        }
        Event event = eventRepository.getReferenceById(eventId);
        event.setState("CANCELED");
        return EventMapper.eventToEventFullDto(eventRepository.save(event), participationRepository
                .countByEvent_IdAndStatusContaining(eventId, "CONFIRMED"));
    }

    @Override
    public List<ParticipationDto> getParticipantsInEventByUser(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        return ParticipationMapper.participantsToParticipantsDto(participationRepository
                .findAllByEvent_IdAndEvent_Initiator_Id(eventId, userId));
    }

    @Override
    @Transactional
    public ParticipationDto confirmRequestInEventByUser(Long userId, Long eventId, Long reqId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        if (!participationRepository.existsById(reqId)) {
            throw new NotFoundException("Запрос на участие с id-" + reqId + " не найден");
        }
        if (participationRepository.countByEvent_IdAndStatusContaining(eventId, "CONFIRMED")
                .equals(eventRepository.getReferenceById(eventId).getParticipantLimit())) {
            throw new EventsException("Лимит заявок на событие id-" + eventId + " уже исчерпан");
        }
        Participation confirmRequest = participationRepository.getReferenceById(reqId);
        confirmRequest.setStatus("CONFIRMED");
        ParticipationDto participationDto = ParticipationMapper.
                participationToParticipationDto(participationRepository.save(confirmRequest));
        if (participationRepository.countByEvent_IdAndStatusContaining(eventId, "CONFIRMED")
                .equals(eventRepository.getReferenceById(eventId).getParticipantLimit())) {
            List<Participation> rejectedRequests = participationRepository.
                    findAllByEvent_IdAndStatusContaining(eventId, "PENDING");
            rejectedRequests.forEach(request -> request.setStatus("REJECTED"));
            participationRepository.saveAll(rejectedRequests);
        }
        return participationDto;
    }

    @Override
    @Transactional
    public ParticipationDto rejectRequestInEventByUser(Long userId, Long eventId, Long reqId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        if (!participationRepository.existsById(reqId)) {
            throw new NotFoundException("Запрос на участие с id-" + reqId + " не найден");
        }
        Participation rejectRequest = participationRepository.getReferenceById(reqId);
        rejectRequest.setStatus("REJECTED");
        return ParticipationMapper.participationToParticipationDto(participationRepository.save(rejectRequest));
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(Set<Long> usersId, Set<String> states, Set<Long> categoriesId,
                                                  String rangeStart, String rangeEnd, int from, int size) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart == null && rangeEnd == null) {
            start = LocalDateTime.now();
            end = LocalDateTime.now().plusYears(100);
        } else if (rangeStart != null && rangeEnd == null) {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.now().plusYears(100);
        } else if (rangeStart == null) {
            start = LocalDateTime.now();
            end = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        Page<Event> eventsPage = eventRepository.getEventsWithStates(usersId,
                states, categoriesId, start, end, PageRequest.of(from, size));
        if (states == null) {
            eventsPage = eventRepository.getEventsWithoutStates(usersId,
                    categoriesId, start, end, PageRequest.of(from, size));
        }
        List<EventFullDto> events = new ArrayList<>();
        for (Event event : eventsPage) {
            events.add(EventMapper.eventToEventFullDto(event, participationRepository
                    .countByEvent_IdAndStatusContaining(event.getId(), "CONFIRMED")));
        }
        return events;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, EventDto eventDto) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Event adminUpdateEvent = eventRepository.getReferenceById(eventId);
        if (!eventDto.getAnnotation().equals(adminUpdateEvent.getAnnotation()) && eventDto.getAnnotation() != null) {
            adminUpdateEvent.setAnnotation(eventDto.getAnnotation());
        }
        if (!eventDto.getCategory().equals(adminUpdateEvent.getCategory().getId()) && eventDto
                .getCategory() != null) {
            adminUpdateEvent.setCategory(categoryRepository.getReferenceById(eventDto.getCategory()));
        }
        if (!eventDto.getDescription().equals(adminUpdateEvent.getDescription()) && eventDto
                .getDescription() != null) {
            adminUpdateEvent.setDescription(eventDto.getDescription());
        }
        if (!eventDto.getEventDate().equals(adminUpdateEvent.getEventDate().format(formatter))
                && eventDto.getEventDate() != null) {
            adminUpdateEvent.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), formatter));
        }
        if (eventDto.getPaid() != (adminUpdateEvent.getPaid()) && eventDto
                .getPaid() != null) {
            adminUpdateEvent.setPaid(eventDto.getPaid());
        }
        if (!eventDto.getParticipantLimit().equals(adminUpdateEvent.getParticipantLimit()) && eventDto
                .getParticipantLimit() != null) {
            adminUpdateEvent.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (!eventDto.getTitle().equals(adminUpdateEvent.getTitle()) && eventDto
                .getTitle() != null) {
            adminUpdateEvent.setTitle(eventDto.getTitle());
        }
        if (eventDto.getLocation() != null && !eventDto.getLocation().getLat().equals(adminUpdateEvent.getLatitude()) &&
                eventDto.getLocation().getLat() != null) {
            adminUpdateEvent.setLatitude(eventDto.getLocation().getLat());
        }
        if (eventDto.getLocation() != null && !eventDto.getLocation().getLon().equals(adminUpdateEvent.getLongitude()) &&
                eventDto.getLocation().getLon() != null) {
            adminUpdateEvent.setLongitude(eventDto.getLocation().getLon());
        }
        return EventMapper.eventToEventFullDto(eventRepository.save(adminUpdateEvent),
                participationRepository.countByEvent_IdAndStatusContaining(adminUpdateEvent.getId(), "CONFIRMED"));
    }

    @Override
    @Transactional
    public EventFullDto publicationEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        if (!eventRepository.getReferenceById(eventId).getState().equals("PENDING")) {
            throw new EventsException("Событие должно быть в статусе ожидания публикации. Статус события: " +
                    eventRepository.getReferenceById(eventId).getState());
        }
        if (eventRepository.getReferenceById(eventId).getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventsException("Дата начала события раньше чем через час");
        }
        Event publicationEvent = eventRepository.getReferenceById(eventId);
        publicationEvent.setState("PUBLISHED");
        return EventMapper.eventToEventFullDto(eventRepository.save(publicationEvent), participationRepository
                .countByEvent_IdAndStatusContaining(eventId, "CONFIRMED"));
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        if (eventRepository.getReferenceById(eventId).getState().equals("PUBLISHED")) {
            throw new EventsException("Событие уже опубликованно");
        }
        Event rejectEvent = eventRepository.getReferenceById(eventId);
        rejectEvent.setState("CANCELED");
        return EventMapper.eventToEventFullDto(eventRepository.save(rejectEvent), participationRepository
                .countByEvent_IdAndStatusContaining(eventId, "CONFIRMED"));
    }

    private Long getViewsByEvent(String uri, String ip) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EndpointHit hit = new EndpointHit();
        hit.setApp("main-service");
        hit.setIp(ip);
        hit.setUri(uri);
        hit.setTimestamp(LocalDateTime.now().format(formatter));
        return Objects.requireNonNull(statsClient.postEndpointHit(hit).getBody()).getHits();
    }
}
