package ru.practicum.explorewithme.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.participation.model.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Integer countByEvent_IdAndStatusContaining(Long eventId, String status);

    List<Participation> findAllByEvent_IdAndStatusContaining(Long eventId, String status);

    List<Participation> findAllByRequester_Id(Long userId);

    List<Participation> findAllByEvent_IdAndEvent_Initiator_Id(Long eventId, Long userId);

    Participation findByRequester_IdAndEvent_Id(Long userId, Long eventId);
}
