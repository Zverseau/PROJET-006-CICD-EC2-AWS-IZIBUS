package com.example.izibus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReserverPayerDto {

    private ReservationDto reservation;
    private String modePaiement;
    private double montant;

}
