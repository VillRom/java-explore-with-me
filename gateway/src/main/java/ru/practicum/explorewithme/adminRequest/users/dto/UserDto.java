package ru.practicum.explorewithme.adminRequest.users.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Data
public class UserDto {

    private Long id;

    @Email
    private String email;

    private String name;
}
