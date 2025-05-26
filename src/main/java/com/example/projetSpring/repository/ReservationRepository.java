package com.example.projetSpring.repository;

import com.example.projetSpring.model.Reservation;
import com.example.projetSpring.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserOrderByDateReservationDesc(Utilisateur user);

    @Query("SELECT SUM(r.nombrePlaces) FROM Reservation r WHERE r.event.id = :eventId ")
    Optional<Integer> sumPlacesReserveesByEventId(@Param("eventId") Long eventId);

}