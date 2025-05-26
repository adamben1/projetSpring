package com.example.projetSpring.controller;

import com.example.projetSpring.dto.CategorieDto;
import com.example.projetSpring.dto.EventDto;
import com.example.projetSpring.dto.LoginDto;
import com.example.projetSpring.dto.RegistrationDto;
import com.example.projetSpring.service.CategorieService;
import com.example.projetSpring.service.EventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final EventService eventService;
    private final CategorieService categorieService;

    public HomeController(EventService eventService,CategorieService categorieService){
        this.categorieService = categorieService;
        this.eventService = eventService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        // Récupérer les événements pour la page d'accueil
        List<EventDto> featuredEvents = eventService.getAllEvents();
        if (featuredEvents.size() > 6) {
            featuredEvents = featuredEvents.subList(0, 6);
        }
        model.addAttribute("featuredEvents", featuredEvents);

        // Récupérer les catégories dynamiquement
        List<CategorieDto> categories = categorieService.getAllCategories();
        model.addAttribute("categories", categories);

        // Message pour le débogage (facultatif)
        if (featuredEvents.isEmpty()) {
            model.addAttribute("debug", "Aucun événement trouvé");
        } else {
            model.addAttribute("debug", featuredEvents.size() + " événements trouvés");
        }
        return "home";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login"; // ta page login (à créer)
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }


}
