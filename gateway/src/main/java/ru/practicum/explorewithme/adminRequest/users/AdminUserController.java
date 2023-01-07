package ru.practicum.explorewithme.adminRequest.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.adminRequest.users.dto.UserDto;
import ru.practicum.explorewithme.client.AdminClient;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminClient adminClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Create User {}", userDto);
        return adminClient.addUserAdmin(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersByIds(@RequestParam Set<Long> ids,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("Get users by set id {}", ids);
        return adminClient.getUsersAdmin(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Delete user dy id {}", userId);
        return adminClient.deleteUserAdmin(userId);
    }
}
