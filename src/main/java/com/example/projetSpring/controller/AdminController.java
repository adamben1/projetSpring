package com.example.projetSpring.controller;

import com.example.projetSpring.exception.AlreadyExistsException;
import com.example.projetSpring.model.Categorie;
import com.example.projetSpring.model.Event;
import com.example.projetSpring.model.Utilisateur;
import com.example.projetSpring.repository.CategorieRepository;
import com.example.projetSpring.repository.EventRepository;
import com.example.projetSpring.repository.UtilisateurRepository;
import com.example.projetSpring.service.AdminService;
import com.example.projetSpring.service.UtilisateurService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final EventRepository eventRepository;
    private final CategorieRepository categorieRepository;

    public AdminController(AdminService adminService, UtilisateurService utilisateurService, UtilisateurRepository utilisateurRepository, EventRepository eventRepository, CategorieRepository categorieRepository) {
        this.adminService = adminService;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.eventRepository = eventRepository;
        this.categorieRepository = categorieRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Statistiques totales
        long totalEvents = eventRepository.count();
        long totalCategories = categorieRepository.count();
        long totalUsers = utilisateurRepository.count();

        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalUsers", totalUsers);

        // Données pour graphique des catégories
        List<Categorie> categories = categorieRepository.findAll();
        List<Map<String, Object>> categoriesData = new ArrayList<>();

        for (Categorie cat : categories) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("nom", cat.getNom());
            categoryMap.put("count", eventRepository.countEventsByCategorieId(cat.getId()));
            categoriesData.add(categoryMap);
        }

        model.addAttribute("categoriesData", categoriesData);

        // Données pour graphique des types d'utilisateurs
        List<Map<String, Object>> userTypesData = new ArrayList<>();
        Map<String, Object> usersMap = new HashMap<>();
        usersMap.put("role", "Utilisateur");
        usersMap.put("count", utilisateurRepository.findByRole("USER").size());

        Map<String, Object> adminsMap = new HashMap<>();
        adminsMap.put("role", "Admin");
        adminsMap.put("count", utilisateurRepository.findByRole("ADMIN").size());

        userTypesData.add(usersMap);
        userTypesData.add(adminsMap);

        model.addAttribute("userTypesData", userTypesData);

        // Événements récents
        List<Event> recentEvents = eventRepository.findAll(PageRequest.of(0, 5, Sort.by("id").descending())).getContent();
        model.addAttribute("recentEvents", recentEvents);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("currentPage", "users");
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/admins")
    public String manageAdmins(Model model) {
        model.addAttribute("currentPage", "admins");
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin/admins";
    }


    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute Utilisateur user, RedirectAttributes redirectAttributes) {
        try {
            // Vérifier si l'email existe déjà
            if (utilisateurRepository.existsByEmail(user.getEmail())) {
                return "redirect:/admin/users?emailError=true";
            }

            // Vérifier si le username existe déjà
            if (utilisateurRepository.existsByUsername(user.getUsername())) {
                return "redirect:/admin/users?usernameError=true";
            }

            // Si tout est ok, sauvegarder l'utilisateur
            utilisateurRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur créé avec succès !");
            return "redirect:/admin/users";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur est survenue lors de la création de l'utilisateur.");
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/admins/create")
    public String createAdmin(@ModelAttribute Utilisateur admin, RedirectAttributes redirectAttributes) {
        try {
            // Vérifier si l'email existe déjà
            if (utilisateurRepository.existsByEmail(admin.getEmail())) {
                return "redirect:/admin/admins?emailError=true";
            }

            // Vérifier si le username existe déjà
            if (utilisateurRepository.existsByUsername(admin.getUsername())) {
                return "redirect:/admin/admins?usernameError=true";
            }

            // S'assurer que le rôle est bien ADMIN
            admin.setRole("ADMIN");

            // Si tout est ok, sauvegarder l'administrateur
            utilisateurRepository.save(admin);
            redirectAttributes.addFlashAttribute("successMessage", "Administrateur créé avec succès !");
            return "redirect:/admin/admins";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur est survenue lors de la création de l'administrateur.");
            return "redirect:/admin/admins";
        }
    }

    @PostMapping("/admins/update/{id}")
    public String updateAdmin(@PathVariable Long id, @ModelAttribute Utilisateur utilisateur) {
        adminService.updateUser(id, utilisateur);
        return "redirect:/admin/admins";
    }

    @PostMapping("/admins/delete/{id}")
    public String deleteAdmin(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteAdmin(id);
            redirectAttributes.addFlashAttribute("successMessage", "Administrateur supprimé avec succès.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/admins"; // redirige vers la liste des admins
    }

    @PostMapping("/promote/{id}")
    public String promoteToAdmin(@PathVariable Long id) {
        adminService.promoteToAdmin(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/demote/{id}")
    public String demoteToUser(@PathVariable Long id) {
        adminService.demoteToUser(id);
        return "redirect:/admin/admins";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Utilisateur currentAdmin = adminService.getUserByUsername(username);

        model.addAttribute("currentPage", "profile");
        model.addAttribute("admin", currentAdmin);
        return "admin/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Utilisateur utilisateur, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Utilisateur currentAdmin = adminService.getUserByUsername(username);

        // Ne pas permettre de changer le rôle via cette méthode
        utilisateur.setRole(currentAdmin.getRole());

        try {
            // Mettre à jour l'utilisateur avec son ID actuel
            adminService.updateUser(currentAdmin.getId(), utilisateur);
            redirectAttributes.addFlashAttribute("successMessage", "Votre profil a été mis à jour avec succès!");
        } catch (AlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/profile";
    }
}
