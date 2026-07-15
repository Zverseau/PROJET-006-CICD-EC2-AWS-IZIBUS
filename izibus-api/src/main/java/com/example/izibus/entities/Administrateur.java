package com.example.izibus.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur extends UserBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nomAdmin;
    @Column(nullable = false)
    private String prenomAdmin;

    @OneToMany(mappedBy ="administrateur" , cascade = CascadeType.ALL)
    private List<Client> clients = new ArrayList<>();
    @OneToMany(mappedBy ="administrateur" , cascade = CascadeType.ALL)
    private List<Compagnie> compagnies = new ArrayList<>();
}
