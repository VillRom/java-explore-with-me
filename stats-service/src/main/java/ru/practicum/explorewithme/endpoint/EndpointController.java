package ru.practicum.explorewithme.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.endpoint.dto.EndpointHitDto;
import ru.practicum.explorewithme.endpoint.dto.ViewsStats;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
public class EndpointController {

    private final EndpointService endpointService;

    @Autowired
    public EndpointController(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @PostMapping(value = "/hit")
    public void addHit(@RequestBody EndpointHitDto hit) {
        log.info("Add endpointHit {}", hit);
        endpointService.addHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewsStats> getViews(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                     @RequestParam(required = false) List<String> uris,
                                     @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get views");
        return endpointService.getHits(start, end, uris, unique);
    }
}
