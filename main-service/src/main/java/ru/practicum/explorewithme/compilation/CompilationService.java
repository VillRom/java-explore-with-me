package ru.practicum.explorewithme.compilation;

import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long compId);

    void removeAnEventFromCompilation(Long compID, Long eventId);

    void addEventToCompilation(Long compID, Long eventId);

    void unpinCompilation(Long compId);

    void pinCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(Long compId);
}
