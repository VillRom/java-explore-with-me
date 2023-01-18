package ru.practicum.explorewithme.location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.location.model.SpecificLocation;

import java.util.Set;

public interface SpecificLocationRepository extends JpaRepository<SpecificLocation, Long> {

    Page<SpecificLocation> findAllByIdIn(Set<Long> ids, Pageable pageable);
}
