package ru.practicum.explorewithme.participation;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.participation.dto.ParticipationDto;
import ru.practicum.explorewithme.participation.model.Participation;
import ru.practicum.explorewithme.users.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ParticipationMapper {

    public static ParticipationDto participationToParticipationDto(Participation participation) {
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setId(participation.getId());
        participationDto.setCreated(participation.getCreated());
        participationDto.setEvent(participation.getEvent().getId());
        participationDto.setRequester(participation.getRequester().getId());
        participationDto.setStatus(participation.getStatus());
        return participationDto;
    }

    public static Participation participationDtoToParticipation(ParticipationDto participationDto, User requester,
                                                                Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = participationDto.getCreated().format(formatter);
        Participation participation = new Participation();
        participation.setId(participationDto.getId());
        participation.setCreated(LocalDateTime.parse(time, formatter));
        participation.setRequester(requester);
        participation.setStatus(participationDto.getStatus());
        participation.setEvent(event);
        return participation;
    }

    public static List<ParticipationDto> participantsToParticipantsDto(List<Participation> participants) {
        return participants.stream().map(ParticipationMapper::participationToParticipationDto)
                .collect(Collectors.toList());
    }
}
