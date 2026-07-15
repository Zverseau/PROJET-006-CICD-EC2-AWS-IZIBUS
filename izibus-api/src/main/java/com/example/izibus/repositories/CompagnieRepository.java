package com.example.izibus.repositories;

import com.example.izibus.entities.Compagnie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompagnieRepository extends JpaRepository<Compagnie, Long> {
    Optional<Compagnie> findByEmail(String email);

    Optional<Compagnie> findByNomCompagnie(String nomCompagnie);

    @Query("SELECT c FROM Compagnie c WHERE c.id = :id")
    Optional<Compagnie> findCompagnieById(@Param("id") Long id);

}
