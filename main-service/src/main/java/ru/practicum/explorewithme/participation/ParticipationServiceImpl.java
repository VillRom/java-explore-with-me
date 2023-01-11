package ru.practicum.explorewithme.participation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.events.EventRepository;
import ru.practicum.explorewithme.events.dto.EventState;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.exception.EventsException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.RequestException;
import ru.practicum.explorewithme.participation.dto.ParticipationDto;
import ru.practicum.explorewithme.participation.model.Participation;
import ru.practicum.explorewithme.users.UserRepository;
import ru.practicum.explorewithme.users.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    public List<ParticipationDto> getAllRequestsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        return ParticipationMapper.participantsToParticipantsDto(participationRepository.findAllByRequester_Id(userId));
    }

    @Override
    public ParticipationDto addRequestByUserForEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id-"
                + userId + "не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id-"
                + eventId + " не найдено"));
        if (participationRepository.findByRequester_IdAndEvent_Id(userId, eventId) != null) {
            throw new EventsException("Запрос на участие в событии: " + event.getTitle() + " от пользователя с id-"
                    + userId + " уже существует");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new EventsException("Инициатор не может подать заявку на своё событие");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventsException("Нельзя подать заявку на неопубликованное событие");
        }
        if (event.getParticipantLimit().equals(participationRepository
                .countByEvent_IdAndStatusContaining(eventId, "CONFIRMED"))) {
            throw new EventsException("Лимит заявок на событие исчерпан");
        }
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setRequester(userId);
        participationDto.setEvent(eventId);
        if (!event.getRequestModeration()) {
            participationDto.setStatus("CONFIRMED");
        } else {
            participationDto.setStatus("PENDING");
        }
        participationDto.setCreated(LocalDateTime.now());
        return ParticipationMapper.participationToParticipationDto(participationRepository.save(ParticipationMapper
                .participationDtoToParticipation(participationDto, user, event)));
    }

    @Override
    public ParticipationDto canselRequestByUserForEvent(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id-" + userId + " не найден");
        }
        Participation participation = participationRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id-" + requestId + " не найден"));
        if (participation.getStatus().equals("PENDING")) {
            participation.setStatus("CANCELED");
            return ParticipationMapper.participationToParticipationDto(participationRepository.save(participation));
        } else {
            throw new RequestException("Статус заявки находится на в ожидании. Статус - " + participation.getStatus());
        }
    }
}
