package ru.practicum.explorewithme.controllers.adminControllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.users.UserService;
import ru.practicum.explorewithme.users.dto.UserDto;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserService userService;

    @Autowired
    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Create User {}", userDto);
        return userService.addUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUsersById(@RequestParam Set<Long> ids,
                                      @RequestParam int from,
                                      @RequestParam int size) {
        log.info("Get users by set id {}", ids);
        return userService.findUsersById(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Delete user dy id {}", userId);
        userService.deleteUserById(userId);
    }
}
