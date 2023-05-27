package ru.practicum.explorewithme.location;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.events.EventRepository;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.exception.LocationException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.location.dto.NewSpecificLocationDto;
import ru.practicum.explorewithme.location.dto.SpecificLocationDto;
import ru.practicum.explorewithme.location.dto.UpdateSpecificLocationDto;
import ru.practicum.explorewithme.location.model.SpecificLocation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpecificLocationServiceImpl implements SpecificLocationService {

    private final SpecificLocationRepository locationRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public SpecificLocationDto addLocation(NewSpecificLocationDto locationDto) {
        return LocationMapper.specificLocationToSpecificLocationDto(locationRepository
                .save(LocationMapper.newSpecificLocationToSpecificLocation(locationDto)));
    }

    @Override
    @Transactional
    public SpecificLocationDto updateLocation(Long locId, UpdateSpecificLocationDto locationDto) {
        SpecificLocation location = locationRepository.findById(locId)
                .orElseThrow(() -> new NotFoundException("Локация с id - " + locId + " не найдена"));
        if (locationDto.getName() != null && !locationDto.getName().equals(location.getName())) {
            location.setName(locationDto.getName());
        }
        if (locationDto.getLatitude() != null && !locationDto.getLatitude().equals(location.getLatitude())) {
            location.setLatitude(locationDto.getLatitude());
            if (location.getEvents() != null) {
                location.setEvents(checkEventsInTheLocation(location.getEvents(), location));
            }
        }
        if (locationDto.getLongitude() != null && !locationDto.getLongitude().equals(location.getLongitude())) {
            location.setLongitude(locationDto.getLongitude());
            if (location.getEvents() != null) {
                location.setEvents(checkEventsInTheLocation(location.getEvents(), location));
            }
        }
        if (locationDto.getRadius() != null && !locationDto.getRadius().equals(location.getRadius())) {
            location.setRadius(locationDto.getRadius());
            if (location.getEvents() != null) {
                location.setEvents(checkEventsInTheLocation(location.getEvents(), location));
            }
        }
        if (locationDto.getEvents() != null && !locationDto.getEvents().equals(location.getEvents())) {
            location.setEvents(checkEventsInTheLocation(locationDto.getEvents(), location));
        }
        return LocationMapper.specificLocationToSpecificLocationDto(locationRepository.save(location));
    }

    private List<Event> checkEventsInTheLocation(List<Event> checkEvents, SpecificLocation location) {
        return checkEvents.stream().filter(event ->
                calculateDistanceBetweenTwoPoints(event.getLatitude(), event.getLongitude(), location.getLatitude(),
                        location.getLongitude()) < location.getRadius()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLocation(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new NotFoundException("Локация с id - " + locationId + " не найдена");
        }
        locationRepository.deleteById(locationId);
    }

    @Override
    public List<SpecificLocationDto> findLocationsByIds(Set<Long> ids, int from, int size) {
        Page<SpecificLocation> locations;
        if (ids == null) {
            locations = locationRepository.findAll(PageRequest.of(from, size));
        } else {
            locations = locationRepository.findAllByIdIn(ids, PageRequest.of(from, size));
        }
        return LocationMapper.locationsToLocationsDto(locations);
    }

    @Override
    public SpecificLocationDto getLocationById(Long id) {
        return LocationMapper.specificLocationToSpecificLocationDto(locationRepository
                .findById(id).orElseThrow(() -> new NotFoundException("Локация с id - " + id + " не найдена")));
    }

    @Override
    @Transactional
    public SpecificLocationDto addEventToLocation(Long locId, Long eventId) {
        SpecificLocation location = locationRepository.findById(locId)
                .orElseThrow(() -> new NotFoundException("Локация с id - " + locId + " не найдена"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id - " + eventId + " не найдено"));
        if (calculateDistanceBetweenTwoPoints(event.getLatitude(), event.getLongitude(),
                location.getLatitude(), location.getLongitude()) > location.getRadius()) {
            throw new LocationException("Событие с id - " + eventId + " находится за пределамим локации");
        }
        location.getEvents().add(event);
        return LocationMapper.specificLocationToSpecificLocationDto(locationRepository.save(location));
    }

    @Override
    @Transactional
    public void deleteEventFromLocation(Long locId, Long eventId) {
        SpecificLocation location = locationRepository.findById(locId)
                .orElseThrow(() -> new NotFoundException("Локация с id - " + locId + " не найдена"));
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id - " + eventId + " не найдено");
        }
        List<Event> events = location.getEvents().stream().filter(event -> !event.getId().equals(eventId))
                .collect(Collectors.toList());
        location.setEvents(events);
        locationRepository.save(location);
    }

    private Double calculateDistanceBetweenTwoPoints(Double lat1, Double lon1, Double lat2, Double lon2) {
        double radLatFirst = Math.PI * lat1 / 180;
        double radLatSec = Math.PI * lat2 / 180;
        double theta = lon1 - lon2;
        double radTheta = Math.PI * theta / 180;
        Double dist = Math.sin(radLatFirst) * Math.sin(radLatSec) + Math.cos(radLatFirst) * Math.cos(radLatSec)
                * Math.cos(radTheta);
        dist = Math.acos(dist);
        System.out.println(dist);
        dist = dist * 180 / Math.PI;
        dist = dist * 60 * 1.8524;
        return dist;
    }
}
