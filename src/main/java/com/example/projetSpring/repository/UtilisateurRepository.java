package com.example.projetSpring.repository;

import com.example.projetSpring.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Utilisateur> findByUsername(String username);
    List<Utilisateur> findByRole(String role);
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);


}
