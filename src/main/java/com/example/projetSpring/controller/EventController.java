package com.example.projetSpring.controller;

import com.example.projetSpring.dto.CategorieDto;
import com.example.projetSpring.dto.EventDto;
import com.example.projetSpring.service.CategorieService;
import com.example.projetSpring.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final CategorieService categorieService;

    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("categories", categorieService.getAllCategories());
        return "admin/events";
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute EventDto eventDto,
                              @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            // Assigner le fichier image au DTO
            eventDto.setImageFile(imageFile);
            eventService.saveEvent(eventDto);
            redirectAttributes.addFlashAttribute("successMessage", "L'événement a été créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la création de l'événement: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id,
                              @ModelAttribute EventDto eventDto,
                              @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            // Assigner le fichier image au DTO
            eventDto.setImageFile(imageFile);
            eventService.updateEvent(id, eventDto);
            redirectAttributes.addFlashAttribute("successMessage", "L'événement a été mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour de l'événement: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "L'événement a été supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression de l'événement: " + e.getMessage());
        }
        return "redirect:/admin/events";
    }

}