package ru.practicum.explorewithme.adminrequest.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.adminrequest.compilations.dto.NewCompilationDto;
import ru.practicum.explorewithme.client.AdminClient;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationsController {

    private final AdminClient adminClient;

    @PostMapping
    public ResponseEntity<Object> addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Add compilation {}", newCompilationDto);
        return adminClient.addCompilationAdmin(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable Long compId) {
        log.info("Delete compilation by id {}", compId);
        return adminClient.deleteCompilationAdmin(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public ResponseEntity<Object> deleteEventFromCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        log.info("Delete event id {} from compilation by id {}", eventId, compId);
        return adminClient.deleteEventFromCompilationAdmin(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public ResponseEntity<Object> addEventToCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        log.info("Add event id {} from compilation by id {}", eventId, compId);
        return adminClient.addEventToCompilationAdmin(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public ResponseEntity<Object> unpinCompilationOnTheMainPage(@PathVariable Long compId) {
        log.info("Unpin a compilation id - {} on the main page", compId);
        return adminClient.unpinCompilationOnTheMainPageAdmin(compId);
    }

    @PatchMapping("/{compId}/pin")
    public ResponseEntity<Object> pinCompilationOnTheMainPage(@PathVariable Long compId) {
        log.info("Pin a compilation id - {} on the main page", compId);
        return adminClient.pinCompilationOnTheMainPageAdmin(compId);
    }
}
