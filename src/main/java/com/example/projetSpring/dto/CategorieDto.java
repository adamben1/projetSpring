package com.example.projetSpring.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategorieDto {
    private Long id;
    private String nom;
    private String description;
    private String icon;
    private Integer nbEvenements;
}

