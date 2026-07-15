package com.example.izibus.repositories;

import com.example.izibus.entities.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    List<Trajet> findByCompagnie_Id(Long id);

    List<Trajet> findByLieuDepart(String lieuDepart);

    List<Trajet> findByLieuArriver(String lieuArriver);

    List<Trajet> findByPrixLessThanEqual(double prixMax);
}
