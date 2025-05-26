package com.example.projetSpring.service;

import com.example.projetSpring.exception.AlreadyExistsException;
import com.example.projetSpring.model.Utilisateur;
import com.example.projetSpring.repository.UtilisateurRepository;
import com.example.projetSpring.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurService utilisateurService;

    public AdminServiceImpl(UtilisateurRepository utilisateurRepository,
                            PasswordEncoder passwordEncoder, UtilisateurService utilisateurService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.utilisateurService = utilisateurService;
    }

    @Override
    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findByRole("USER");
    }

    @Override
    public List<Utilisateur> getAllAdmins() {
        return utilisateurRepository.findByRole("ADMIN");
    }

    @Override
    public Utilisateur createUser(Utilisateur utilisateur) {
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur updateUser(Long id, Utilisateur utilisateur) throws AlreadyExistsException {
        Utilisateur existingUser = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        // Vérifier si l'email existe déjà pour un autre utilisateur
        if (utilisateurRepository.existsByEmailAndIdNot(utilisateur.getEmail(), id)) {
            throw new AlreadyExistsException("L'adresse email " + utilisateur.getEmail() + " est déjà utilisée par un autre compte.");
        }

        // Si le mot de passe est vide, gardez l'ancien mot de passe
        if (utilisateur.getPassword() == null || utilisateur.getPassword().isEmpty()) {
            utilisateur.setPassword(existingUser.getPassword());
        } else {
            // Sinon, vous devriez hacher le nouveau mot de passe ici
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }

        utilisateur.setId(id);
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public void deleteUser(Long id) {
        utilisateurRepository.deleteById(id);
    }

    @Override
    public Utilisateur promoteToAdmin(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setRole("ADMIN");
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur demoteToUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateur.setRole("USER");
        return utilisateurRepository.save(utilisateur);
    }

    public void deleteAdmin(Long adminId) {
        if (!utilisateurRepository.existsById(adminId)) {
            throw new RuntimeException("Administrateur non trouvé avec l'id : " + adminId);
        }
        utilisateurRepository.deleteById(adminId);
    }

    public Utilisateur getUserByUsername(String username) {
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le nom d'utilisateur: " + username));
    }
}
