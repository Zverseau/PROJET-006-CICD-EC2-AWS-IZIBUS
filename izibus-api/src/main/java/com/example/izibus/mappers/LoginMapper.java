package com.example.izibus.mappers;

import com.example.izibus.dto.LoginRequest;
import com.example.izibus.entities.UserBase;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LoginMapper {

    // Mapper pour la Connexion
    UserBase toDto(LoginRequest loginRequest);
    LoginRequest toEntity(UserBase userBase);
}
