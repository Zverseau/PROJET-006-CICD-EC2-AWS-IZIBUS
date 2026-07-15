package com.example.izibus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationStatDto {
    long today;
    long week;
    long month;
    long year;
    long cancelledToday;
}
