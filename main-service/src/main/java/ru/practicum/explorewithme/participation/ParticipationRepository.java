package ru.practicum.explorewithme.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.participation.dto.RequestStatus;
import ru.practicum.explorewithme.participation.model.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("select count(p) from Participation p where p.event.id = ?1 and p.status = ?2")
    Integer countByEvent_IdAndStatus(Long eventId, RequestStatus status);

    @Query("select p.event.id AS id, count(p) AS count from Participation p where p.event.id in ?1 and p.status = ?2 group by p.event.id")
    List<CountParticipation> getIds(List<Long> eventId, RequestStatus status);

    interface CountParticipation {

        Long getId();

        Integer getCount();
    }

    List<Participation> findAllByEvent_IdAndStatus(Long eventId, RequestStatus status);

    List<Participation> findAllByRequester_Id(Long userId);

    List<Participation> findAllByEvent_IdAndEvent_Initiator_Id(Long eventId, Long userId);

    Participation findByRequester_IdAndEvent_Id(Long userId, Long eventId);
}
