package ru.practicum.explorewithme.controllers.adminControllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.compilation.CompilationService;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
public class CompilationAdminController {

    private final CompilationService compilationService;

    @Autowired
    public CompilationAdminController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    public CompilationDto addCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("Add compilation {}", newCompilationDto);
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        log.info("Delete compilation by id {}", compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        compilationService.removeAnEventFromCompilation(compId, eventId);
        log.info("Delete event id {} from compilation by id {}", eventId, compId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        compilationService.addEventToCompilation(compId, eventId);
        log.info("Add event id {} from compilation by id {}", eventId, compId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinCompilationOnTheMainPage(@PathVariable Long compId) {
        compilationService.unpinCompilation(compId);
        log.info("Unpin a compilation id - {} on the main page", compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilationOnTheMainPage(@PathVariable Long compId) {
        compilationService.pinCompilation(compId);
        log.info("Pin a compilation id - {} on the main page", compId);
    }
}
