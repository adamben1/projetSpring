package com.example.projetSpring.service;

import com.example.projetSpring.dto.RegistrationDto;
import com.example.projetSpring.dto.LoginDto;
import com.example.projetSpring.exception.AuthenticationException;
import com.example.projetSpring.mapper.UtilisateurMapper;
import com.example.projetSpring.model.Utilisateur;
import com.example.projetSpring.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    // Méthode pour enregistrer un nouvel utilisateur avec rôle USER
    public Utilisateur registerUser(RegistrationDto registrationDto) {
        // Vérifier si l'utilisateur existe déjà (par exemple par email ou username)
        if (utilisateurRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris");
        }
        if (utilisateurRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        // Mapper DTO vers entity
        Utilisateur utilisateur = utilisateurMapper.toEntity(registrationDto);

        // Encoder le mot de passe
        utilisateur.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Forcer le rôle USER (ignore celui passé dans le DTO pour sécurité)
        utilisateur.setRole("USER");

        // Sauvegarder dans la base
        return utilisateurRepository.save(utilisateur);
    }


    public Utilisateur loginUser(LoginDto loginDto) {
        // Trouver l'utilisateur par username
        Utilisateur utilisateur = utilisateurRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new AuthenticationException("Nom d'utilisateur ou mot de passe incorrect"));

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(loginDto.getPassword(), utilisateur.getPassword())) {
            throw new AuthenticationException("Nom d'utilisateur ou mot de passe incorrect");
        }

        return utilisateur;
    }

    public Utilisateur getUserById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public Utilisateur findByUsername(String name) {
        return utilisateurRepository.findByUsername(name)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}

