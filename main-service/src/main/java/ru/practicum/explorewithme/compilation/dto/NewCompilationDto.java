package ru.practicum.explorewithme.compilation.dto;

import lombok.Data;
import ru.practicum.explorewithme.compilation.model.Compilation;

import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link Compilation} entity
 */
@Data
public class NewCompilationDto implements Serializable {

    private Long id;

    private List<Long> events;

    private Boolean pinned;

    private String title;
}