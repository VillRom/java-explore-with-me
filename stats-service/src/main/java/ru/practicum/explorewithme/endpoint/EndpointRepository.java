package ru.practicum.explorewithme.endpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.endpoint.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EndpointRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select e from EndpointHit e where e.timestamp > ?1 and e.timestamp < ?2 and e.uri in ?3")
    List<EndpointHit> getEndpointHitsWithTimeIntervalAndUris(LocalDateTime start, LocalDateTime end,
                                                             Collection<String> uris);
}
