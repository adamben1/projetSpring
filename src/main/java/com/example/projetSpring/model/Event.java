package com.example.projetSpring.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evenements")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private String lieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @Column(unique = true)
    private String externalId;

    private String source;

    @Column(nullable = false)
    private Double prixMin;

    @Column(nullable = false)
    private Double prixMax;

    @Column(nullable = false)
    private Integer capacite; // nombre de places/disponibles

    @Column(nullable = false)
    private String statut; // "À venir", "En cours", "Terminé"

    // Nouveau champ pour l'URL de l'image
    private String imageUrl;
}
