package ru.practicum.explorewithme.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.location.dto.NewSpecificLocationDto;
import ru.practicum.explorewithme.location.dto.SpecificLocationDto;
import ru.practicum.explorewithme.location.dto.UpdateSpecificLocationDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/location")
public class SpecificLocationAdminController {

    private final SpecificLocationService service;

    @Autowired
    public SpecificLocationAdminController(SpecificLocationService service) {
        this.service = service;
    }

    @PostMapping
    public SpecificLocationDto addLocation(@RequestBody @Valid NewSpecificLocationDto locationDto) {
        log.info("Add Location {}", locationDto);
        return service.addLocation(locationDto);
    }

    @PutMapping("/{locId}")
    public SpecificLocationDto updateLocation(@PathVariable Long locId,
                                              @RequestBody UpdateSpecificLocationDto locationDto) {
        log.info("Update Location {}", locationDto);
        return service.updateLocation(locId, locationDto);
    }

    @DeleteMapping("/{locId}")
    public void deleteLocation(@PathVariable Long locId) {
        service.deleteLocation(locId);
        log.info("Delete location with id - {}", locId);
    }

    @GetMapping
    public List<SpecificLocationDto> findLocations(@RequestParam(required = false) Set<Long> ids,
                                                   @RequestParam int from,
                                                   @RequestParam int size) {
        if (ids == null) {
            log.info("Get all locations");
        } else {
            log.info("Get find locations - {}", ids);
        }
        return service.findLocationsByIds(ids, from, size);
    }

    @GetMapping("/{locId}")
    public SpecificLocationDto getLocationById(@PathVariable Long locId) {
        log.info("Get location by id - {}", locId);
        return service.getLocationById(locId);
    }

    @PatchMapping("/{locId}/events/{eventId}")
    public SpecificLocationDto addEventToLocation(@PathVariable Long locId, @PathVariable Long eventId) {
        SpecificLocationDto locationDto = service.addEventToLocation(locId, eventId);
        log.info("Add event - {} to location - {}", eventId, locId);
        return locationDto;
    }

    @DeleteMapping("/{locId}/events/{eventId}")
    public void deleteEventsToLocation(@PathVariable Long locId, @PathVariable Long eventId) {
        service.deleteEventFromLocation(locId, eventId);
        log.info("Add events - {} to location - {}", eventId, locId);
    }
}
