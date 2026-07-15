package com.example.izibus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrajetDto {

    private Long id;

    // ← Champ date ajouté
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String nomCompagnie; // Ajouté

    //private List<Reservation> reservations;

    private Long compagnieId;

    private int nombrePlaces;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private Date heureDepart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private Date heureArriver;

    private String lieuDepart;

    private String lieuArriver;

    private double prix;

}
