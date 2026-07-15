package com.example.izibus.dto;

import lombok.Data;

@Data
public class AdministrateurDTO {
    private Long id;
    private String email;
    private String nomAdmin;
    private String prenomAdmin;
    private String role;
}