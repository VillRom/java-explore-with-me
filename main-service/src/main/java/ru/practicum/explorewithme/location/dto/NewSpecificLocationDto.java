package ru.practicum.explorewithme.location.dto;

import com.sun.istack.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.explorewithme.location.model.SpecificLocation} entity
 */
@Data
public class NewSpecificLocationDto implements Serializable {

    @NotNull
    private String name;

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    @NotNull
    private Integer radius;
}