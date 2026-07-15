package com.example.izibus.mappers;

import com.example.izibus.dto.TrajetDto;
import com.example.izibus.entities.Trajet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TrajetMapper {

    Trajet toEntity(TrajetDto trajetDto);
    TrajetDto toDto(Trajet trajet);
}
