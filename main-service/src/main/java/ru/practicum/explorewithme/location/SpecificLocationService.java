package ru.practicum.explorewithme.location;

import ru.practicum.explorewithme.location.dto.NewSpecificLocationDto;
import ru.practicum.explorewithme.location.dto.SpecificLocationDto;
import ru.practicum.explorewithme.location.dto.UpdateSpecificLocationDto;

import java.util.List;
import java.util.Set;

public interface SpecificLocationService {

    SpecificLocationDto addLocation(NewSpecificLocationDto locationDto);

    SpecificLocationDto updateLocation(Long locId, UpdateSpecificLocationDto locationDto);

    void deleteLocation(Long locationId);

    List<SpecificLocationDto> findLocationsByIds(Set<Long> ids, int from, int size);

    SpecificLocationDto getLocationById(Long id);

    SpecificLocationDto addEventToLocation(Long locId, Long eventId);

    void deleteEventFromLocation(Long locId, Long eventId);
}
