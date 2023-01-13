package ru.practicum.explorewithme.events;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.categories.CategoryRepository;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.events.dto.*;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.events.model.QEvent;
import ru.practicum.explorewithme.exception.EventsException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.RequestException;
import ru.practicum.explorewithme.participation.ParticipationMapper;
import ru.practicum.explorewithme.participation.ParticipationRepository;
import ru.practicum.explorewithme.participation.dto.ParticipationDto;
import ru.practicum.explorewithme.participation.dto.RequestStatus;
import ru.practicum.explorewithme.participation.model.Participation;
import ru.practicum.explorewithme.statsclient.StatsClient;
import ru.practicum.explorewithme.statsclient.endpoint.EndpointHit;
import ru.practicum.explorewithme.statsclient.endpoint.ViewsStats;
import ru.practicum.explorewithme.users.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final ParticipationRepository participationRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getPublishedEventsWithFilter(String text, Set<Long> categoriesId, Boolean paid,
                                                            LocalDateTime rangeStart, LocalDateTime  rangeEnd,
                                                            Boolean onlyAvailable, String sort, int from, int size,
                                                            HttpServletRequest request) {
        rangeStart = rangeStart == null ? LocalDateTime.now().minusYears(5) : rangeStart;
        rangeEnd = rangeEnd == null ? LocalDateTime.now().plusYears(5) : rangeEnd;
        QEvent event = QEvent.event;
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(event.state.eq(EventState.PUBLISHED));
        predicates.add(event.eventDate.after(rangeStart));
        predicates.add(event.eventDate.before(rangeEnd));
        if (text != null) {
            predicates.add(event.annotation.likeIgnoreCase(text));
        }
        if (categoriesId != null) {
            predicates.add(event.category.id.in(categoriesId));
        }
        if (paid != null) {
            predicates.add(event.paid.eq(paid));
        }
        Page<Event> eventPage = eventRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicates)),
                PageRequest.of(from, size));
        List<String> uris = new ArrayList<>();
        for (Event eventUri : eventPage) {
            saveView(request.getRequestURI() + "/" + eventUri.getId(), request.getRemoteAddr());
            uris.add(request.getRequestURI() + "/" +  eventUri.getId());
        }
        List<ViewsStats> views = getViewsByEvent(rangeStart, rangeEnd, uris);
        for (Event eventView : Objects.requireNonNull(eventPage)) {
            eventView.setViews(views.stream()
                    .filter(viewsStats -> viewsStats.getUri().equals(request.getRequestURI() + "/" + eventView.getId()))
                    .findFirst().get().getHits());
        }
        if (eventPage.getSize() > 1) {
            if (sort != null && sort.equals("EVENT_DATE")) {
                eventPage.stream().sorted(Comparator.comparing(Event::getEventDate));
            } else if (sort != null && sort.equals("VIEWS")) {
                eventPage.stream().sorted(Comparator.comparingLong(Event::getViews));
            }
        }
        return EventMapper.eventsToEventsShortDto(eventPage.stream().collect(Collectors.toList()),
                participationRepository.getIds(eventPage.stream().map(Event::getId).collect(Collectors.toList()),
                        RequestStatus.CONFIRMED));
    }

    @Override
    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id-" + eventId + " не найдено");
        }
        Event event = eventRepository.findEventByIdAndStateContainsIgnoreCase(eventId, EventState.PUBLISHED.name());
        event.setViews(getViewsByEvent(LocalDateTime.now().minusYears(5), LocalDateTime.now().plusSeconds(2),
                List.of(request.getRequestURI())).get(0).getHits());
        return EventMapper.eventToEventFullDto(event,
                participationRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED));
    }

    @Override
    @Transactional
    public EventFullDto addEvent(EventDto eventDto, Long userId) {
        if (eventDto.getAnnotation() == null || eventDto.getCategory() == null || eventDto.getDescription() == null ||
                eventDto.getEventDate() == null || eventDto.getLocation() == null || eventDto.getTitle() == null) {
            throw new RequestException("Пустыми могут быть только поля paid, participantLimit, requestModeration");
        }
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(() ->
                new RequestException("Категория с id-" + eventDto.getCategory() + " не найдена"));
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventsException("Время начала события не может быть раньше, чем через 2 часа");
        }
        Event event = EventMapper.newEventDtoToEvent(eventDto, category, userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id-" + userId + " не найден")));
        event.setCreationOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
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
                    .countByEvent_IdAndStatus(event.getId(), RequestStatus.CONFIRMED)));
        }
        return eventsShortDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        Event updateEvent = eventRepository.findById(updateEventRequest.getEventId()).orElseThrow(() ->
                new NotFoundException("Событие с id-" + updateEventRequest.getEventId() + " не найдено"));

        if (updateEvent.getState() == (EventState.PUBLISHED)) {
            throw new EventsException("Событие нельзя изменить, т.к. оно уже опубликованно");
        }
        if (updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventsException("Время начала события не может быть раньше, чем через 2 часа");
        }
        if (updateEventRequest.getAnnotation() != null && !updateEventRequest.getAnnotation()
                .equals(updateEvent.getAnnotation())) {
            updateEvent.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null && !updateEventRequest.getCategory().equals(updateEvent
                .getCategory().getId())) {
            updateEvent.setCategory(categoryRepository.findById(updateEventRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категория не найдена")));
        }
        if (updateEventRequest.getDescription() != null && !updateEventRequest.getDescription()
                .equals(updateEvent.getDescription())) {
            updateEvent.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null && !updateEventRequest.getEventDate().equals(updateEvent
                .getEventDate())) {
            updateEvent.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getPaid() != null && updateEventRequest.getPaid() != (updateEvent.getPaid())) {
            updateEvent.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null && !updateEventRequest.getParticipantLimit()
                .equals(updateEvent.getParticipantLimit())) {
            updateEvent.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getTitle() != null && !updateEventRequest.getTitle().equals(updateEvent.getTitle())) {
            updateEvent.setTitle(updateEventRequest.getTitle());
        }
        if (updateEvent.getState() == (EventState.CANCELED)) {
            updateEvent.setState(EventState.PENDING);
        }
        return EventMapper.eventToEventFullDto(eventRepository.save(updateEvent),
                participationRepository.countByEvent_IdAndStatus(updateEvent.getId(), RequestStatus.CONFIRMED));
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
                participationRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED));
    }

    @Override
    @Transactional
    public EventFullDto canceledEventByUser(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id-" + eventId + " не найдено"));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new RequestException("Пользователь с id-" + userId + " не создатель события");
        }
        if (event.getState() != (EventState.PENDING)) {
            throw new EventsException("Событие нельзя отменить, т.к. его статус - "
                    + event.getState());
        }
        event.setState(EventState.CANCELED);
        return EventMapper.eventToEventFullDto(eventRepository.save(event), participationRepository
                .countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED));
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
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id-" + eventId + " не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new RequestException("Пользователь с id-" + userId + " не создатель события");
        }
        Participation confirmRequest = participationRepository.findById(reqId).orElseThrow(() ->
                new NotFoundException("Запрос на участие с id-" + reqId + " не найден"));
        if (participationRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED)
                .equals(event.getParticipantLimit())) {
            throw new EventsException("Лимит заявок на событие id-" + eventId + " уже исчерпан");
        }
        confirmRequest.setStatus(RequestStatus.CONFIRMED);
        ParticipationDto participationDto = ParticipationMapper
                .participationToParticipationDto(participationRepository.save(confirmRequest));
        if (participationRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED) >=
                (event.getParticipantLimit())) {
            changeStatusOfParticipantToRejected(eventId);
        }
        return participationDto;
    }

    @Override
    @Transactional
    public ParticipationDto rejectRequestInEventByUser(Long userId, Long eventId, Long reqId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id-" + eventId + " не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new RequestException("Пользователь с id-" + userId + " не создатель события");
        }
        Participation rejectRequest = participationRepository.findById(reqId).orElseThrow(() ->
                new NotFoundException("Запрос на участие с id-" + reqId + " не найден"));
        rejectRequest.setStatus(RequestStatus.REJECTED);
        return ParticipationMapper.participationToParticipationDto(participationRepository.save(rejectRequest));
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(Set<Long> usersId, List<EventState> states, Set<Long> categoriesId,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        rangeStart = rangeStart == null ? LocalDateTime.now().minusYears(5) : rangeStart;
        rangeEnd = rangeEnd == null ? LocalDateTime.now().plusYears(5) : rangeEnd;
        QEvent event = QEvent.event;
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(event.state.eq(EventState.PUBLISHED));
        predicates.add(event.eventDate.after(rangeStart));
        predicates.add(event.eventDate.before(rangeEnd));
        if (usersId != null) {
            predicates.add(event.initiator.id.in(usersId));
        }
        if (states != null) {
            predicates.add(event.state.in(states));
        }
        if (categoriesId != null) {
            predicates.add(event.category.id.in(categoriesId));
        }
        Page<Event> eventPage = eventRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicates)),
                PageRequest.of(from, size));
        return EventMapper.eventsToEventsFullDto(eventPage.stream().collect(Collectors.toList()),
                participationRepository.getIds(eventPage.stream().map(Event::getId).collect(Collectors.toList()),
                        RequestStatus.CONFIRMED));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, EventDto eventDto) {
        Event adminUpdateEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id-" + eventId + " не найдено"));
        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().equals(adminUpdateEvent.getAnnotation())) {
            adminUpdateEvent.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getCategory() != null && !eventDto.getCategory().equals(adminUpdateEvent.getCategory().getId())) {
            adminUpdateEvent.setCategory(categoryRepository.findById(eventDto.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категория с id-" + eventDto.getCategory() + " не найдена")));
        }
        if (eventDto.getDescription() != null && !eventDto.getDescription().equals(adminUpdateEvent.getDescription())) {
            adminUpdateEvent.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null && !eventDto.getEventDate().equals(adminUpdateEvent.getEventDate())) {
            adminUpdateEvent.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getPaid() != null && eventDto.getPaid() != (adminUpdateEvent.getPaid())) {
            adminUpdateEvent.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null && !eventDto.getParticipantLimit().equals(adminUpdateEvent
                .getParticipantLimit())) {
            adminUpdateEvent.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getTitle() != null && !eventDto.getTitle().equals(adminUpdateEvent.getTitle())) {
            adminUpdateEvent.setTitle(eventDto.getTitle());
        }
        if (eventDto.getLocation() != null && eventDto.getLocation().getLat() != null &&
                !eventDto.getLocation().getLat().equals(adminUpdateEvent.getLatitude())) {
            adminUpdateEvent.setLatitude(eventDto.getLocation().getLat());
        }
        if (eventDto.getLocation() != null && eventDto.getLocation().getLon() != null &&
                !eventDto.getLocation().getLon().equals(adminUpdateEvent.getLongitude())) {
            adminUpdateEvent.setLongitude(eventDto.getLocation().getLon());
        }
        return EventMapper.eventToEventFullDto(eventRepository.save(adminUpdateEvent),
                participationRepository.countByEvent_IdAndStatus(adminUpdateEvent.getId(), RequestStatus.CONFIRMED));
    }

    @Override
    @Transactional
    public EventFullDto publicationEvent(Long eventId) {
        Event publicationEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id-" + eventId + " не найдено"));
        if (publicationEvent.getState() != (EventState.PENDING)) {
            throw new EventsException("Событие должно быть в статусе ожидания публикации. Статус события: " +
                    publicationEvent.getState());
        }
        if (publicationEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventsException("Дата начала события раньше чем через час");
        }
        publicationEvent.setState(EventState.PUBLISHED);
        return EventMapper.eventToEventFullDto(eventRepository.save(publicationEvent), participationRepository
                .countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED));
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(Long eventId) {
        Event rejectEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id-" + eventId + " не найдено"));
        if (rejectEvent.getState() == (EventState.PUBLISHED)) {
            throw new EventsException("Событие уже опубликованно");
        }
        rejectEvent.setState(EventState.CANCELED);
        return EventMapper.eventToEventFullDto(eventRepository.save(rejectEvent), participationRepository
                .countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED));
    }

    private List<ViewsStats> getViewsByEvent(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<String> uris) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = rangeStart == null ? LocalDateTime.now().minusYears(5).format(formatter) : rangeStart.format(formatter);
        String end = rangeEnd == null ? LocalDateTime.now().plusSeconds(2).format(formatter) : rangeEnd.format(formatter);
        return statsClient.getViews(start, end, uris, false);
    }

    private void saveView(String uri, String ip) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EndpointHit hit = new EndpointHit();
        hit.setApp("main-service");
        hit.setIp(ip);
        hit.setUri(uri);
        hit.setTimestamp(LocalDateTime.now().format(formatter));
        statsClient.postEndpointHit(hit);
    }

    private void changeStatusOfParticipantToRejected(Long eventId) {
        List<Participation> rejectedRequests = participationRepository
                .findAllByEvent_IdAndStatus(eventId, RequestStatus.PENDING);
        rejectedRequests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
        participationRepository.saveAll(rejectedRequests);
    }
}
