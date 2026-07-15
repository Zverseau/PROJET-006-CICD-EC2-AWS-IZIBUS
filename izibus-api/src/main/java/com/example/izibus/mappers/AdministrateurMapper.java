package com.example.izibus.mappers;

import com.example.izibus.dto.AdministrateurDTO;
import com.example.izibus.entities.Administrateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AdministrateurMapper {

    @Mapping(target = "role", expression = "java(\"ADMIN\")")
    AdministrateurDTO toDto(Administrateur administrateur);

    @Mapping(target = "clients", ignore = true)
    @Mapping(target = "compagnies", ignore = true)
    Administrateur toEntity(AdministrateurDTO administrateurDTO);
}