package ru.practicum.explorewithme.participation;

import ru.practicum.explorewithme.participation.dto.ParticipationDto;

import java.util.List;

public interface ParticipationService {

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    List<ParticipationDto> getAllRequestsByUser(Long userId);

    //Добавление запроса от текущего пользователя на участие в событии
    ParticipationDto addRequestByUserForEvent(Long userId, Long eventId);

    //Отмена своего запроса на участие в событии
    ParticipationDto canselRequestByUserForEvent(Long userId, Long eventId);
}
