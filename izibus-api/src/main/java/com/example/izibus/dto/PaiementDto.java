package com.example.izibus.dto;

import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDto {
    private Long id;
    private Long reservationId;
    private double montant;
    private String modePaiement;

}
