package ru.practicum.explorewithme.compilation.dto;

import lombok.Data;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.users.model.User;

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

    @Data
    public static class EventShortDto {

        private Long id;

        private String annotation;

        private Integer confirmedRequests;

        private Category category;

        private String eventDate;

        private User initiator;

        private Boolean paid;

        private String title;

        private Long views;
    }
}