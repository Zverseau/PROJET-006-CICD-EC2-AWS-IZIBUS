package com.example.izibus.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompagnieResponse {
    private Long id;
    private String nomCompagnie;
    private String description;
    private String telephoneCompagnie;
    private String logoCompagnie;
    private String email;
}