package com.example.izibus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompagnieRegisterRequest extends RegisterRequest{
    private Long id;
    private String nomCompagnie;
    private String description;
    private String telephoneCompagnie;
    private String logoCompagnie;
}
