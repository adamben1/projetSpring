package com.example.projetSpring.controller;

import com.example.projetSpring.model.Event;
import com.example.projetSpring.model.Reservation;
import com.example.projetSpring.model.Utilisateur;
import com.example.projetSpring.service.EventService;
import com.example.projetSpring.service.ReservationService;
import com.example.projetSpring.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final EventService eventService;
    private final UtilisateurService userService;

    @PostMapping("/reserver")
    @ResponseBody
    public ResponseEntity<?> reserverEvent(@RequestBody Map<String, Object> requestData,
                                           Authentication authentication) {
        try {
            // Vérifier si l'utilisateur est connecté
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Vous devez être connecté pour effectuer une réservation"));
            }

            Long eventId = Long.valueOf(requestData.get("eventId").toString());
            Integer nombrePlaces = Integer.valueOf(requestData.get("nombrePlaces").toString());

            // Récupérer l'utilisateur connecté
            Utilisateur user = userService.findByUsername(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Utilisateur non trouvé"));
            }

            // Récupérer l'événement
            Event event = eventService.findById(eventId);
            if (event == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Événement non trouvé"));
            }

            // Créer la réservation
            Reservation reservation = reservationService.creerReservation(user, event, nombrePlaces);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Réservation confirmée !",
                    "numeroReservation", reservation.getNumeroReservation(),
                    "prixTotal", reservation.getPrixTotal()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Une erreur est survenue lors de la réservation"));
        }
    }

    @GetMapping("/mes-reservations")
    public String mesReservations(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Utilisateur user = userService.findByUsername(authentication.getName());
            if (user != null) {
                List<Reservation> reservations = reservationService.findByUser(user);
                model.addAttribute("reservations", reservations);

                // Calculs pour les statistiques
                int totalPlaces = reservations.stream().mapToInt(Reservation::getNombrePlaces).sum();
                double totalMontant = reservations.stream().mapToDouble(Reservation::getPrixTotal).sum();

                model.addAttribute("totalPlaces", totalPlaces);
                model.addAttribute("totalMontant", totalMontant);
            }
        }
        return "public/mes-reservations";
    }

    @PostMapping("/annuler/{id}")
    public String annulerReservation(@PathVariable Long id,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            Utilisateur user = userService.findByUsername(authentication.getName());
            reservationService.annulerReservation(id, user);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Votre réservation a été annulée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de l'annulation : " + e.getMessage());
        }
        return "redirect:/reservations/mes-reservations";
    }
}