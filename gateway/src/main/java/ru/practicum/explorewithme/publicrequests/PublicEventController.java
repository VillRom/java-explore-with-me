package ru.practicum.explorewithme.publicrequests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.client.PublicClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class PublicEventController {

    private final PublicClient publicClient;

    @GetMapping()
    public ResponseEntity<Object> getEventsWithFilter(@RequestParam(required = false) String text,
                                                      @RequestParam(required = false) Set<Long> categories,
                                                      @RequestParam(required = false) Boolean paid,
                                                      @RequestParam(required = false) String rangeStart,
                                                      @RequestParam(required = false) String rangeEnd,
                                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                      @RequestParam(required = false) String sort,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "1000") int size) {
        log.info("Get events with filter");
        return publicClient.getEventsWithFilterPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFullInfoAboutEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Get full info about event Id {}", id);
        return publicClient.getFullInfoAboutEventByIdPublic(id, request);
    }
}
