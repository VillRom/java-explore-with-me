package ru.practicum.explorewithme.location.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.explorewithme.events.model.Event;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "specific_location")
public class SpecificLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double longitude;

    private Double latitude;

    private Integer radius;

    @ManyToMany
    @JoinTable(name = "event_location",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "longitude = " + longitude + ", " +
                "latitude = " + latitude + ", " +
                "radius = " + radius + ")";
    }
}
