package ru.practicum.explorewithme.users;

import ru.practicum.explorewithme.users.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {

    UserDto addUser(UserDto user);

    List<UserDto> findUsersById(Set<Long> usersId, int from, int size);

    void deleteUserById(Long id);
}
