package ru.practicum.explorewithme.compilation.dto;

import lombok.Data;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.events.dto.EventShortDto;

import java.util.List;

/**
 * A DTO for the {@link Compilation} entity
 */
@Data
public class CompilationDto {

    private Long id;

    private List<EventShortDto> events;

    private Boolean pinned;

    private String title;
}