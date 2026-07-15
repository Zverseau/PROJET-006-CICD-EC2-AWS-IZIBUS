package com.example.izibus.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegisterRequest extends RegisterRequest{
    private Long id;
    private String nomClient;
    private String prenomClient;
    private String telephoneClient;
    private String verificationCode;

}
