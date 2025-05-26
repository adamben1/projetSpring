package com.example.projetSpring.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "categories")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String description; // ex: "Catégorie de football", "Catégorie de basketball", etc.

    // Pour afficher une icône par catégorie
    private String icon; // ex: "fa-futbol", "fa-basketball-ball", etc.

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Event> evenements;
}
