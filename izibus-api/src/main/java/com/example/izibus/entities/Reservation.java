package com.example.izibus.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation extends AuditTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compagnie_id", nullable = false)
    private Compagnie compagnie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Paiement> paiements;
    @Column(nullable = false)
    private String nomPassager;
    @Column(nullable = false)
    private String prenomPassager;
    @Column(nullable = false)
    private String telephonePassager;
    @Column(nullable = false)
    private int nombrePlacesReservees;
    @Column(nullable = false)
    private double montantTotal;
    @Column(nullable = false)
    private boolean annulee;
}