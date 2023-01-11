package ru.practicum.explorewithme.privaterequest.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.client.PrivateClient;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateRequestController {

    private final PrivateClient privateClient;

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Object> getRequestsByUser(@PathVariable Long userId) {
        log.info("Get requests by user id - {}", userId);
        return privateClient.getRequestsByUserPrivate(userId);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Object> addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Add request by user id - {} and event id - {}", userId, eventId);
        return privateClient.addRequestPrivate(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Cancel request with id - {}", requestId);
        return privateClient.cancelRequestPrivate(userId, requestId);
    }
}
