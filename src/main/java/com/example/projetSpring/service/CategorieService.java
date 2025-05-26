package com.example.projetSpring.service;

import com.example.projetSpring.dto.CategorieDto;
import com.example.projetSpring.mapper.CategorieMapper;
import com.example.projetSpring.model.Categorie;
import com.example.projetSpring.repository.CategorieRepository;
import com.example.projetSpring.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorieService {
    private final CategorieRepository categorieRepository;
    private final CategorieMapper categorieMapper;
    private final EventRepository eventRepository;

    public List<CategorieDto> getAllCategories() {
        List<Categorie> cats = categorieRepository.findAll();
        List<CategorieDto> dtos = cats.stream().map(categorieMapper::toDto).collect(Collectors.toList());
        for (CategorieDto dto : dtos) {
            // Ici on compte vraiment le nombre d'événements en base
            dto.setNbEvenements(eventRepository.countEventsByCategorieId(dto.getId()));
        }
        return dtos;
    }

    public CategorieDto saveCategorie(CategorieDto dto) {
        if (categorieRepository.existsByNomIgnoreCase(dto.getNom())) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà !");
        }
        Categorie categorie = categorieMapper.toEntity(dto);
        Categorie saved = categorieRepository.save(categorie);
        return categorieMapper.toDto(saved);
    }

    public void deleteCategorie(Long id) {
        int nbEvents = eventRepository.countEventsByCategorieId(id);
        if (nbEvents > 0) {
            throw new IllegalStateException("Impossible de supprimer une categorie qui contient des evenements !");
        }
        categorieRepository.deleteById(id);
    }

    public void updateCategorie(Long id, CategorieDto dto) {
        Optional<Categorie> existing = categorieRepository.findByNomIgnoreCase(dto.getNom());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà !");
        }
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        categorie.setNom(dto.getNom());
        categorie.setDescription(dto.getDescription());
        categorie.setIcon(dto.getIcon());
        categorieRepository.save(categorie);
    }

}
