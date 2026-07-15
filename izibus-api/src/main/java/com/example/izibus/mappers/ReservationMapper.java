package com.example.izibus.mappers;

import com.example.izibus.dto.ReservationDto;
import com.example.izibus.entities.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReservationMapper {

    Reservation toEntity(ReservationDto reservationDto);
    ReservationDto toDto(Reservation reservation);




}
