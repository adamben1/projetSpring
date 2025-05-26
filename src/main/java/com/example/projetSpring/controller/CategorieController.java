package com.example.projetSpring.controller;

import com.example.projetSpring.dto.CategorieDto;
import com.example.projetSpring.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategorieController {
    private final CategorieService categorieService;

    @GetMapping
    public String listCategories(Model model,
                                 @RequestParam(value = "errorMessage", required = false) String errorMessage) {
        model.addAttribute("categories", categorieService.getAllCategories());
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        return "admin/categories";
    }

    @PostMapping("/create")
    public String createCategorie(@ModelAttribute CategorieDto categorieDto, Model model) {
        try {
            categorieService.saveCategorie(categorieDto);
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categorieService.getAllCategories());
            return "admin/categories";
        }
    }

    @PostMapping("/update/{id}")
    public String updateCategorie(@PathVariable Long id,
                                  @ModelAttribute CategorieDto categorieDto,
                                  Model model) {
        try {
            categorieService.updateCategorie(id, categorieDto);
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categorieService.getAllCategories());
            return "admin/categories";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCategorie(@PathVariable Long id, Model model) {
        try {
            categorieService.deleteCategorie(id);
            return "redirect:/admin/categories";
        } catch (IllegalStateException e) {
            // Redirige vers la liste avec message d'erreur en paramètre
            return "redirect:/admin/categories?errorMessage=" + e.getMessage();
        }
    }
}