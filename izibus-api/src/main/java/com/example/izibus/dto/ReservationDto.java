package com.example.izibus.dto;

import com.example.izibus.entities.Client;
import com.example.izibus.entities.Trajet;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long id;
    private Long clientId;
    private Long trajetId;
    private Long compagnieId;
    private String nomPassager;
    private String prenomPassager;
    private String telephonePassager;
    private int nombrePlacesReservees;
    private double montantTotal;
    private Boolean annulee;


    private String nomCompagnie; // Ajouté

    // ← Nouveaux champs pour l’affichage  dans le frontend
    private String lieuDepart;
    private String lieuArriver;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateTrajet;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateReservation;
    // Ajouter les heures de trajet
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime heureDepart;

    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime heureArriver;

    // Nouveaux champs pour les réservations clients
    private String nomClient;
    private String prenomClient;
    private String telephoneClient;

}
