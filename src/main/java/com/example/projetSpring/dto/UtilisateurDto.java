package com.example.projetSpring.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDto {

    private Long id;
    private String nom;
    private String prenom;
    private String username;
    private String email;
    private String role;

}
