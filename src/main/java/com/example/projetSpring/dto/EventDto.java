package com.example.projetSpring.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class EventDto {
    private Long id;
    private String nom;
    private String description;
    private String lieu;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String categorieNom;
    private String categorieIcon;
    private Double prixMin;
    private Double prixMax;
    private Integer capacite;
    private String statut;

    private String imageUrl;

    // Ajout du champ pour le fichier image (non persisté)
    private transient MultipartFile imageFile;
}
