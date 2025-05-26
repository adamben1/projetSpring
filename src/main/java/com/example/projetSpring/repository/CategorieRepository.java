package com.example.projetSpring.repository;

import com.example.projetSpring.dto.CategorieDto;
import com.example.projetSpring.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Optional<Categorie> findByNom(String nom);
    boolean existsByNomIgnoreCase(String nom);
    Optional<Categorie> findByNomIgnoreCase(String nom);

}

