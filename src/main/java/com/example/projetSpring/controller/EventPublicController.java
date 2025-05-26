package com.example.projetSpring.controller;

import com.example.projetSpring.dto.CategorieDto;
import com.example.projetSpring.dto.EventDto;
import com.example.projetSpring.service.CategorieService;
import com.example.projetSpring.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService eventService;
    private final CategorieService categorieService;

    @GetMapping
    public String getAllEvents(Model model) {
        List<EventDto> events = eventService.getAllEvents();
        List<CategorieDto> categories = categorieService.getAllCategories();

        model.addAttribute("events", events);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", "Tous les événements");

        return "public/events";
    }

    @GetMapping("/category/{categoryId}")
    public String getEventsByCategory(@PathVariable Long categoryId, Model model) {
        List<EventDto> events = eventService.getEventsByCategory(categoryId);
        List<CategorieDto> categories = categorieService.getAllCategories();

        // Trouver le nom de la catégorie sélectionnée
        String selectedCategoryName = categories.stream()
                .filter(cat -> cat.getId().equals(categoryId))
                .map(CategorieDto::getNom)
                .findFirst()
                .orElse("Catégorie inconnue");

        model.addAttribute("events", events);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", selectedCategoryName);
        model.addAttribute("selectedCategoryId", categoryId);

        return "public/events";
    }

    @GetMapping("/{id}")
    public String getEventDetails(@PathVariable Long id, Model model) {
        EventDto event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "public/event-details";
    }
}