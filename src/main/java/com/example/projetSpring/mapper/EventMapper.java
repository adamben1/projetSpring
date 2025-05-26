package com.example.projetSpring.mapper;

import com.example.projetSpring.dto.EventDto;
import com.example.projetSpring.model.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "categorie.nom", target = "categorieNom")
    @Mapping(source = "categorie.icon", target = "categorieIcon")
    EventDto toDto(Event event);

    @Mapping(target = "categorie", ignore = true) // handled in service
    Event toEntity(EventDto dto);
}

