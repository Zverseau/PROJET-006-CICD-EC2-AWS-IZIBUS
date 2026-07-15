package com.example.izibus.repositories;

import com.example.izibus.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByTelephoneClient(String telephoneClient);

    // Nombre de clients créés un jour donné
    @Query("SELECT COUNT(c) FROM Client c WHERE FUNCTION('DATE', c.createDate) = :date")
    Long countClientsByDay(@Param("date") LocalDate date);

    // Nombre de clients créés dans un mois
    @Query("SELECT COUNT(c) FROM Client c WHERE FUNCTION('MONTH', c.createDate) = :month AND FUNCTION('YEAR', c.createDate) = :year")
    Long countClientsByMonth(@Param("month") int month, @Param("year") int year);

    // Nombre de clients créés dans une année
    @Query("SELECT COUNT(c) FROM Client c WHERE FUNCTION('YEAR', c.createDate) = :year")
    Long countClientsByYear(@Param("year") int year);

    // Ajoutez cette méthode
    long count();



}
