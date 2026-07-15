package com.example.izibus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatDto {
    private Long clientByDay;
    private Long clientByMonth;
    private Long clientByYear;

}
