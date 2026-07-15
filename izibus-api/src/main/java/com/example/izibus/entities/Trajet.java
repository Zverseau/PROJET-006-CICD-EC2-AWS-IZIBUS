package com.example.izibus.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trajet extends AuditTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ← Nouveau champ date
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @OneToMany(mappedBy = "trajet")
    private List<Reservation> reservations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "compagnie_id", nullable = false)
    private Compagnie compagnie;

    @Column(nullable = false)
    private int nombrePlaces;

    @Temporal(TemporalType.TIME)
    @Column(nullable = false)
    private Date heureDepart;

    @Temporal(TemporalType.TIME)
    @Column(nullable = false)
    private Date heureArriver;

    @Column(nullable = false)
    private String lieuDepart;

    @Column(nullable = false)
    private String lieuArriver;

    @Column(nullable = false)
    private double prix;


}
