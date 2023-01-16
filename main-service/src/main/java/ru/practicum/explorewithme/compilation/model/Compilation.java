package ru.practicum.explorewithme.compilation.model;

import lombok.*;
import ru.practicum.explorewithme.events.model.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilation")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "event_compilation",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;

    private Boolean pinned;

    private String title;
}
