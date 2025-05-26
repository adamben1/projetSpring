package com.example.projetSpring.service;

import com.example.projetSpring.model.Event;
import com.example.projetSpring.model.Reservation;
import com.example.projetSpring.model.Utilisateur;
import com.example.projetSpring.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation creerReservation(Utilisateur user, Event event, Integer nombrePlaces) {
        // Vérifications
        if (nombrePlaces <= 0) {
            throw new IllegalArgumentException("Le nombre de places doit être positif");
        }

        // Calculer le prix total (prix moyen entre min et max)
        Double prixUnitaire = (event.getPrixMin() + event.getPrixMax()) / 2;
        Double prixTotal = prixUnitaire * nombrePlaces;

        // Créer la réservation
        Reservation reservation = Reservation.builder()
                .user(user)
                .event(event)
                .nombrePlaces(nombrePlaces)
                .prixTotal(prixTotal)
                .dateReservation(LocalDateTime.now())
                .numeroReservation(genererNumeroReservation())
                .build();

        return reservationRepository.save(reservation);
    }

    public Integer getPlacesReserveesParEvent(Long eventId) {
        return reservationRepository.sumPlacesReserveesByEventId(eventId).orElse(0);
    }

    public List<Reservation> getReservationsParUser(Utilisateur user) {
        return reservationRepository.findByUserOrderByDateReservationDesc(user);
    }

    private String genererNumeroReservation() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public List<Reservation> findByUser(Utilisateur user) {
        return reservationRepository.findByUserOrderByDateReservationDesc(user);
    }

    public void annulerReservation(Long reservationId, Utilisateur user) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation non trouvée"));

        if (!reservation.getUser().equals(user)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à annuler cette réservation");
        }

        // Vérifier si l'annulation est encore possible (ex: 24h avant l'événement)
        // Logique métier selon vos règles

        reservationRepository.delete(reservation);
    }
}