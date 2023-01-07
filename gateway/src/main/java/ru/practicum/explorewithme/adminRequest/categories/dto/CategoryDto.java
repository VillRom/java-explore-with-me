package ru.practicum.explorewithme.adminRequest.categories.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CategoryDto {

    private Long id;

    @NotNull
    private String name;
}
