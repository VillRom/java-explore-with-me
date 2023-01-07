package ru.practicum.explorewithme.endpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.endpoint.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EndpointRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select count(e) from EndpointHit e where upper(e.app) = upper(?1) and upper(e.uri) = upper(?2)")
    Long countByAppIgnoreCaseAndUriIgnoreCase(String app, String uri);


    @Query("select distinct e from EndpointHit e where e.timestamp > ?1 and e.timestamp < ?2 and e.uri in ?3")
    List<EndpointHit> getDistinctByTimestampIsAfterAndTimestampIsBeforeAndUriIsIn(LocalDateTime start,
                                                                                  LocalDateTime end,
                                                                                  Collection<String> uris);

    @Query("select e from EndpointHit e where e.timestamp > ?1 and e.timestamp < ?2 and e.uri in ?3")
    List<EndpointHit> getEndpointHitsByTimestampIsAfterAndTimestampIsBeforeAndUriIsIn(LocalDateTime start,
                                                                                      LocalDateTime end,
                                                                                      Collection<String> uris);

    @Query("select e from EndpointHit e where e.timestamp > ?1 and e.timestamp < ?2")
    List<EndpointHit> getEndpointHitsByTimestampIsAfterAndTimestampIsBefore(LocalDateTime start, LocalDateTime end);



    @Query("select distinct e from EndpointHit e where e.timestamp > ?1 and e.timestamp < ?2")
    List<EndpointHit> getDistinctByTimestampIsAfterAndTimestampIsBefore(LocalDateTime start, LocalDateTime end);
}
