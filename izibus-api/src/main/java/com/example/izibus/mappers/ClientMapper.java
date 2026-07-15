package com.example.izibus.mappers;

import com.example.izibus.dto.ClientDto;
import com.example.izibus.entities.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClientMapper {
    ClientDto toDto(Client client);
    Client toEntity(ClientDto clientDto);
}
