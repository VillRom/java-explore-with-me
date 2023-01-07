package ru.practicum.explorewithme.controllers.privateControllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.participation.ParticipationService;
import ru.practicum.explorewithme.participation.dto.ParticipationDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class ParticipationPrivateController {

    private final ParticipationService participationService;

    @Autowired
    public ParticipationPrivateController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationDto> getRequestsByUser(@PathVariable Long userId) {
        return participationService.getAllRequestsByUser(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return participationService.addRequestByUserForEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return participationService.canselRequestByUserForEvent(userId, requestId);
    }
}
