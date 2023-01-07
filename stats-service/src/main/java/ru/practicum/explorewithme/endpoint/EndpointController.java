package ru.practicum.explorewithme.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.endpoint.dto.EndpointHitDto;
import ru.practicum.explorewithme.endpoint.dto.ViewsStats;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class EndpointController {

    private final EndpointService endpointService;

    @Autowired
    public EndpointController(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @PostMapping(value = "/hit")
    public ViewsStats addHit(@RequestBody EndpointHitDto hit) {
        log.info("Add endpointHit {}", hit);
        return endpointService.addHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewsStats> getViews(@RequestParam String start, @RequestParam String end,
                                     @RequestParam(required = false) Set<String> uris,
                                     @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get views");
        return endpointService.getHits(start, end, uris, unique);
    }
}
