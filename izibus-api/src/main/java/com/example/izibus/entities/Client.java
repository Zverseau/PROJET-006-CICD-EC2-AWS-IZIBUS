package com.example.izibus.entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client extends AuditTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nomClient;
    @Column(nullable = false)
    private String prenomClient;
    @Column(nullable = false)
    private String telephoneClient;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false)
    private boolean isVerified = false;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Administrateur administrateur;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Reservation> reservations;




}
