package com.example.projetSpring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {

    @NotBlank(message = "Nom d'utilisateur obligatoire")
    private String username;

    @NotBlank(message = "Mot de passe obligatoire")
    private String password;

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @NotBlank(message = "Prénom obligatoire")
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    private String email;

}
