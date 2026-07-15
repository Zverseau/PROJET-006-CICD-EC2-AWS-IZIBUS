package com.example.izibus.mappers;

import com.example.izibus.dto.PaiementDto;
import com.example.izibus.entities.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PaiementMapper {

    Paiement toEntity(PaiementDto paiementDto);
    PaiementDto toDto(Paiement paiement);
}
