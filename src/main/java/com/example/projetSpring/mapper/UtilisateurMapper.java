package com.example.projetSpring.mapper;

import com.example.projetSpring.dto.LoginDto;
import com.example.projetSpring.dto.RegistrationDto;
import com.example.projetSpring.model.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {
    Utilisateur toEntity(RegistrationDto dto);

    RegistrationDto toDto(Utilisateur entity);

    LoginDto entityToLoginDto(Utilisateur entity);

    // Pour la conversion LoginDto -> Utilisateur (pour l'authentification)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "nom", ignore = true)
    @Mapping(target = "prenom", ignore = true)
    @Mapping(target = "role", ignore = true)
    Utilisateur loginToEntity(LoginDto dto);
}


