package com.example.projetSpring.repository;

import com.example.projetSpring.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByExternalId(String externalId);
    @Query("SELECT COUNT(e) FROM Event e WHERE e.categorie.id = :catId")
    int countEventsByCategorieId(@Param("catId") Long catId);

    // Compte total d'événements
    long count();

    List<Event> findByCategorieId(Long categorieId);

}