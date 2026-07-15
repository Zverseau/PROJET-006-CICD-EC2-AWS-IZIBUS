package com.example.izibus.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compagnie extends UserBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nomCompagnie;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String telephoneCompagnie;
    @Column(nullable = false)
    private String logoCompagnie;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Administrateur administrateur;
}
