package com.example.projetSpring.mapper;

import com.example.projetSpring.dto.CategorieDto;
import com.example.projetSpring.model.Categorie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategorieMapper {
    CategorieDto toDto(Categorie categorie);
    Categorie toEntity(CategorieDto dto);
}
