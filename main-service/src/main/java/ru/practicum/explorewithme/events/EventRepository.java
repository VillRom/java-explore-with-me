package ru.practicum.explorewithme.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explorewithme.events.model.Event;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("select e from Event e where e.id = ?1 and upper(e.state) like upper(concat('%', ?2, '%'))")
    Event findEventByIdAndStateContainsIgnoreCase(Long id, String state);

    Page<Event> findAllByInitiator_Id(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiator_Id(Long eventId, Long userId);

    Long countByCategory_Id(Long catId);
}
