package ru.practicum.explorewithme.location;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import ru.practicum.explorewithme.location.dto.NewSpecificLocationDto;
import ru.practicum.explorewithme.location.dto.SpecificLocationDto;
import ru.practicum.explorewithme.location.model.SpecificLocation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class LocationMapper {

    public static SpecificLocation newSpecificLocationToSpecificLocation(NewSpecificLocationDto locationDto) {
        SpecificLocation location = new SpecificLocation();
        location.setEvents(Collections.emptyList());
        location.setName(locationDto.getName());
        location.setLatitude(locationDto.getLatitude());
        location.setLongitude(locationDto.getLongitude());
        location.setRadius(locationDto.getRadius());
        return location;
    }

    public static SpecificLocationDto specificLocationToSpecificLocationDto(SpecificLocation location) {
        SpecificLocationDto locationDto = new SpecificLocationDto();
        locationDto.setId(location.getId());
        locationDto.setEvents(location.getEvents());
        locationDto.setName(location.getName());
        locationDto.setRadius(location.getRadius());
        locationDto.setLongitude(location.getLongitude());
        locationDto.setLatitude(location.getLatitude());
        return locationDto;
    }

    public static List<SpecificLocationDto> locationsToLocationsDto(Page<SpecificLocation> locations) {
        return locations.stream().map(LocationMapper::specificLocationToSpecificLocationDto).collect(Collectors.toList());
    }
}
