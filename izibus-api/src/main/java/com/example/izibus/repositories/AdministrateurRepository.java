package com.example.izibus.repositories;

import com.example.izibus.entities.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
    Optional<Administrateur> findByEmail(String email);
    List<Administrateur> findByNomAdminContainingIgnoreCase(String nom);
}