package com.example.izibus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompagnieUpdateRequest {
    private String nomCompagnie;
    private String description;
    private String telephoneCompagnie;
    private String logoCompagnie;

}