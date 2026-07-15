package com.example.izibus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdministrateurRegisterRequest extends RegisterRequest{
    private Long id;
    private String nomAdmin;
    private String prenomAdmin;

}
