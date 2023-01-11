package ru.practicum.explorewithme.adminrequest.compilations.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class NewCompilationDto implements Serializable {

    private Long id;

    private List<Long> events;

    private Boolean pinned;

    @NotNull
    private String title;
}
