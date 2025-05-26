package com.example.projetSpring.controller;

import com.example.projetSpring.dto.LoginDto;
import com.example.projetSpring.dto.RegistrationDto;
import com.example.projetSpring.model.Utilisateur;
import com.example.projetSpring.repository.UtilisateurRepository;
import com.example.projetSpring.service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurService utilisateurService, PasswordEncoder passwordEncoder, UtilisateurRepository utilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            utilisateurService.registerUser(registrationDto);
        } catch (IllegalArgumentException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "register";
        }
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("loginDto") LoginDto loginDto,
                            HttpSession session) {
        Utilisateur utilisateur = utilisateurService.loginUser(loginDto);
        session.setAttribute("user", utilisateur);

        // Redirection basée sur le rôle
        if (utilisateur.getRole().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/home";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Passer l'utilisateur directement avec l'attribut "utilisateur"
        model.addAttribute("utilisateur", utilisateur);

        // Optionnellement, ajouter des statistiques
        // model.addAttribute("totalReservations", /* calculer le nombre */);
        // model.addAttribute("eventsAttended", /* calculer le nombre */);
        // model.addAttribute("recentBookings", /* récupérer les dernières réservations */);

        return "public/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Utilisateur utilisateur,
                                RedirectAttributes redirectAttributes,
                                @RequestParam(required = false) String newPassword,
                                Authentication authentication) {
        try {
            // Récupérer l'utilisateur existant par son username depuis l'authentification
            String currentUsername = authentication.getName();
            Utilisateur existingUser = utilisateurRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Mettre à jour les champs
            existingUser.setNom(utilisateur.getNom());
            existingUser.setPrenom(utilisateur.getPrenom());
            existingUser.setEmail(utilisateur.getEmail());

            // Mettre à jour le mot de passe si fourni
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(newPassword));
            }

            utilisateurRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour : " + e.getMessage());
        }

        return "redirect:/profile";
    }
}