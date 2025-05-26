package com.example.projetSpring.service;

import com.example.projetSpring.dto.EventDto;
import com.example.projetSpring.mapper.EventMapper;
import com.example.projetSpring.model.Categorie;
import com.example.projetSpring.model.Event;
import com.example.projetSpring.repository.CategorieRepository;
import com.example.projetSpring.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategorieRepository categorieRepository;
    private final EventMapper eventMapper;
    private final StorageService storageService;

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventDto saveEvent(EventDto dto) {
        // Gérer l'upload d'image si présent
        processImageUpload(dto);

        // Convertir et sauvegarder l'événement avec le mapper existant
        Event event = eventMapper.toEntity(dto);

        // Récupérer la catégorie
        Categorie categorie = categorieRepository.findByNom(dto.getCategorieNom())
                .orElseGet(() -> categorieRepository.save(Categorie.builder().nom(dto.getCategorieNom()).build()));

        // Définir la catégorie et l'URL de l'image
        event.setCategorie(categorie);
        event.setImageUrl(dto.getImageUrl());

        // Sauvegarder l'événement
        Event saved = eventRepository.save(event);
        return eventMapper.toDto(saved);
    }

    @Transactional
    public void updateEvent(Long id, EventDto eventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Gérer l'image
        if (eventDto.getImageFile() != null && !eventDto.getImageFile().isEmpty()) {
            // Si nouvelle image, supprimer l'ancienne et uploader la nouvelle
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                storageService.delete(event.getImageUrl());
            }
            processImageUpload(eventDto);
        } else {
            // Conserver l'URL d'image actuelle si pas de nouvelle image
            eventDto.setImageUrl(event.getImageUrl());
        }

        // Met à jour les champs
        event.setNom(eventDto.getNom());
        event.setLieu(eventDto.getLieu());
        event.setDateDebut(eventDto.getDateDebut());
        event.setDateFin(eventDto.getDateFin());
        event.setPrixMin(eventDto.getPrixMin());
        event.setPrixMax(eventDto.getPrixMax());
        event.setCapacite(eventDto.getCapacite());
        event.setStatut(eventDto.getStatut());
        event.setImageUrl(eventDto.getImageUrl());

        // Récupère la catégorie associée
        Categorie categorie = categorieRepository.findByNom(eventDto.getCategorieNom())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        event.setCategorie(categorie);

        eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Supprimer l'image associée si elle existe
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            storageService.delete(event.getImageUrl());
        }

        eventRepository.deleteById(id);
    }

    private void processImageUpload(EventDto eventDto) {
        MultipartFile imageFile = eventDto.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = storageService.store(imageFile);
            eventDto.setImageUrl(imageUrl);
        }
    }

    public EventDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID : " + id));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByCategory(Long categoryId) {
        List<Event> events = eventRepository.findByCategorieId(categoryId);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public Event findById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));
    }
}