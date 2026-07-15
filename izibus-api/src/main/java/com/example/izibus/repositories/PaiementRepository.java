package com.example.izibus.repositories;

import com.example.izibus.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByReservation_Id(Long id);

    List<Paiement> findAll();

    List<Paiement> findByReservation_Client_Id(Long id);

    // Chiffre d’affaires par jour
    @Query(value = "SELECT DATE(p.createDate) as date, SUM(p.montant) as total " +
            "FROM paiement p GROUP BY DATE(p.createDate) ORDER BY date DESC",
            nativeQuery = true)
    List<Object[]> getChiffreAffaireByDay();

    // Chiffre d’affaires par mois
    @Query("SELECT FUNCTION('YEAR', p.createDate) as annee, FUNCTION('MONTH', p.createDate) as mois, SUM(p.montant) as total " +
            "FROM Paiement p GROUP BY annee, mois ORDER BY annee DESC, mois DESC")
    List<Object[]> getChiffreAffaireByMonth();

    // Chiffre d’affaires par année
    @Query("SELECT FUNCTION('YEAR', p.createDate) as annee, SUM(p.montant) as total " +
            "FROM Paiement p GROUP BY annee ORDER BY annee DESC")
    List<Object[]> getChiffreAffaireByYear();


    // METHODES POUR LES STATISTIQUES DE CHIFFRES D'AFFAIRES DE COMPAGNIES

    // chiffre d'affaire par jour
    @Query("SELECT SUM(p.montant) FROM Paiement p " +
            "WHERE p.reservation.compagnie.id = :compagnieId " +
            "AND DATE(p.createDate) = CURRENT_DATE")
    Double getChiffreAffaireAujourdhuiByCompagnie(@Param("compagnieId") Long compagnieId);

    // chiffre d'affaire par semaine
    @Query("SELECT SUM(p.montant) FROM Paiement p " +
            "WHERE p.reservation.compagnie.id = :compagnieId " +
            "AND YEAR(p.createDate) = YEAR(CURRENT_DATE) " +
            "AND WEEK(p.createDate) = WEEK(CURRENT_DATE)")
    Double getChiffreAffaireCetteSemaineByCompagnie(@Param("compagnieId") Long compagnieId);

    // chiffre d'affaire par mois
    @Query("SELECT SUM(p.montant) FROM Paiement p " +
            "WHERE p.reservation.compagnie.id = :compagnieId " +
            "AND YEAR(p.createDate) = YEAR(CURRENT_DATE) " +
            "AND MONTH(p.createDate) = MONTH(CURRENT_DATE)")
    Double getChiffreAffaireCeMoisByCompagnie(@Param("compagnieId") Long compagnieId);

    // chiffre d'affaire par années
    @Query("SELECT SUM(p.montant) FROM Paiement p " +
            "WHERE p.reservation.compagnie.id = :compagnieId " +
            "AND YEAR(p.createDate) = YEAR(CURRENT_DATE)")
    Double getChiffreAffaireCetteAnneeByCompagnie(@Param("compagnieId") Long compagnieId);



}
