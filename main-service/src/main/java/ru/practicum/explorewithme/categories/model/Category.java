package ru.practicum.explorewithme.categories.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
