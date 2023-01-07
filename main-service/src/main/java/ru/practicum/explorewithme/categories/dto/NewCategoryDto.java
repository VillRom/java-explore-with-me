package ru.practicum.explorewithme.categories.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.explorewithme.categories.model.Category} entity
 */
@Data
public class NewCategoryDto implements Serializable {

    private final String name;
}