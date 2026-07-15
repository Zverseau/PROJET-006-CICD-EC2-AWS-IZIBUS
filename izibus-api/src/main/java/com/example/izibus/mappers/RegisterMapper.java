package com.example.izibus.mappers;


import com.example.izibus.dto.AdministrateurRegisterRequest;
import com.example.izibus.dto.ClientRegisterRequest;
import com.example.izibus.dto.CompagnieRegisterRequest;
import com.example.izibus.entities.Administrateur;
import com.example.izibus.entities.Client;
import com.example.izibus.entities.Compagnie;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@Component
public interface RegisterMapper {

    // Mapper pour Client
    Client toEntity(ClientRegisterRequest registerRequest);
    ClientRegisterRequest toDto(Client client);

    // Mapper pour Admin
    Administrateur toEntity(AdministrateurRegisterRequest registerRequest);
    AdministrateurRegisterRequest toDto(Administrateur administrateur);

    // Mapper pour Compagnie
    Compagnie toEntity(CompagnieRegisterRequest registerRequest);
    CompagnieRegisterRequest toDto(Compagnie compagnie);
}
