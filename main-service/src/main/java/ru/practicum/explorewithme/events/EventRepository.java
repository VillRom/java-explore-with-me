package ru.practicum.explorewithme.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.events.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    Event findEventByIdAndStateContainsIgnoreCase(Long eventId, String state);

    @Query("select e from Event e " +
            "where e.category.id in ?1 and e.paid = ?2 and e.eventDate > ?3 and e.eventDate < ?4 " +
            "and upper(e.description) like upper(concat('%', ?5, '%')) or upper(e.annotation) " +
            "like upper(concat('%', ?6, '%')) " +
            "order by e.eventDate DESC")
    Page<Event> getEventsWithSortEventDate(Set<Long> catId, Boolean paid, LocalDateTime start, LocalDateTime end,
                                           String text, String textAnnotation, Pageable pageable);

    @Query("select e from Event e " +
            "where e.category.id in ?1 and e.paid = ?2 and e.eventDate > ?3 and e.eventDate < ?4 " +
            "and upper(e.description) like upper(concat('%', ?5, '%')) or upper(e.annotation) " +
            "like upper(concat('%', ?6, '%')) " +
            "order by e.views DESC")
    Page<Event> getEventsWithSortViews(Set<Long> catId, Boolean paid, LocalDateTime start, LocalDateTime end,
                                       String text, String textTitle, Pageable pageable);

    Page<Event> findAllByInitiator_Id(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiator_Id(Long eventId, Long userId);

    @Query("select e from Event e " +
            "where e.initiator.id in ?1 and e.state in ?2 and e.category.id in ?3 and e.eventDate > ?4 and e.eventDate < ?5")
    Page<Event> getEventsWithStates(Collection<Long> initiatorId, Collection<String> state,
                                    Collection<Long> categoryId, LocalDateTime eventDate, LocalDateTime eventDate2,
                                    Pageable pageable);

    @Query("select e from Event e " +
            "where e.initiator.id in ?1 and e.category.id in ?2 and e.eventDate > ?3 and e.eventDate < ?4")
    Page<Event> getEventsWithoutStates(Collection<Long> initiatorId, Collection<Long> categoryId,
                                       LocalDateTime eventDate, LocalDateTime eventDate2, Pageable pageable);

    Long countByCategory_Id(Long catId);
}
