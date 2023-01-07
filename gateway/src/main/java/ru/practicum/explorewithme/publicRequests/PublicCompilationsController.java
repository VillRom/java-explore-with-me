package ru.practicum.explorewithme.publicRequests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.client.PublicClient;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
public class PublicCompilationsController {

    private final PublicClient publicClient;

    @GetMapping()
    public ResponseEntity<Object> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "1000") int size) {
        log.info("Get compilations with pinned = {}", pinned);
        return publicClient.getCompilationsPublic(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<Object> getCompilationById(@PathVariable Long compId) {
        log.info("Get compilation by id {}", compId);
        return publicClient.getCompilationByIdPublic(compId);
    }
}
