package com.example.izibus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaiementStatDto {
    private String periode;
    private double total;
}
