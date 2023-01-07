package ru.practicum.explorewithme.users.dto;

import lombok.Data;
import ru.practicum.explorewithme.users.model.User;

import java.io.Serializable;

/**
 * A DTO for the {@link User} entity
 */
@Data
public class UserShortDto implements Serializable {
    private final Long id;
    private final String name;
}