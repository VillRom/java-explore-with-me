package ru.practicum.explorewithme.events.model;

import lombok.Data;
import ru.practicum.explorewithme.categories.model.Category;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations;

    private LocalDateTime creationOn;

    private String description;

    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private String state;

    private String title;

    private Long views;

    private Double longitude;

    private Double latitude;
}
