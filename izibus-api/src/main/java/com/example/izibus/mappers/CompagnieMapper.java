package com.example.izibus.mappers;

import com.example.izibus.dto.CompagnieResponse;
import com.example.izibus.entities.Compagnie;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CompagnieMapper {

    CompagnieResponse toResponse(Compagnie compagnie);

    // Optionnel : si tu veux mapper dans l’autre sens
    Compagnie toEntity(CompagnieResponse dto);
}
