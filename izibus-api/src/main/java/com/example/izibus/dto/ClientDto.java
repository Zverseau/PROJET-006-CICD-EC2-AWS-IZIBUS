package com.example.izibus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto implements Serializable {
    private Long id;
    private String nomClient;
    private String prenomClient;
    private String telephoneClient;
    private boolean verified;

    @Override
    public String toString() {
        return "ClientDto{" +
                "id=" + id +
                ", nomClient='" + nomClient + '\'' +
                ", prenomClient='" + prenomClient + '\'' +
                ", telephoneClient='" + telephoneClient + '\'' +
                ", verified=" + verified +
                '}';
    }
}
