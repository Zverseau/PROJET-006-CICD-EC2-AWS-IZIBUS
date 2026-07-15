package com.example.izibus.repositories;

import com.example.izibus.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByClient_Id(Long id);

    List<Reservation> findByTrajet_Id(Long id);

    List<Reservation> findByCompagnie_Id(Long compagnieId);


    //Nombre de reservations par jour
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.compagnie.id = ?1 AND FUNCTION('DATE', r.createDate) = FUNCTION('CURRENT_DATE')")
    long countReservationByDay(Long compagnieId);

    //Nombre de reservation en 1 semaine
    @Query("SELECT COUNT (r) FROM Reservation r WHERE r.compagnie.id = ?1 AND FUNCTION('WEEK', r.createDate) = FUNCTION('WEEK', CURRENT_DATE) AND FUNCTION('YEAR', r.createDate) = FUNCTION('YEAR' , CURRENT_DATE)")
    Long countReservationByWeek(Long compagnieId);

    //Nombnre de reservations par mois
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.compagnie.id = ?1 AND FUNCTION('MONTH', r.createDate) = FUNCTION('MONTH', CURRENT_DATE) AND FUNCTION('YEAR', r.createDate) = FUNCTION('YEAR', CURRENT_DATE)")
    long countReservationsThisMonth(Long compagnieId);


    //Nombnre de reservations par an
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.compagnie.id = ?1 AND FUNCTION('YEAR', r.createDate) = FUNCTION('YEAR', CURRENT_DATE)")
    long countReservationsThisYear(Long compagnieId);

    //Nombnre de reservations annulées
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.compagnie.id = ?1 AND r.annulee = true AND FUNCTION('DATE', r.createDate) = FUNCTION('CURRENT_DATE')")
    long countCancelledReservationsToday(Long compagnieId);


    // méthodes pour les statistiques globales

    // nombre de reservation journalier
    @Query("SELECT COUNT(r) FROM Reservation r WHERE FUNCTION('DATE', r.createDate) = FUNCTION('CURRENT_DATE')")
    long countGlobalReservationByDay();

    // nombre de reservation de la semaine
    @Query("SELECT COUNT(r) FROM Reservation r WHERE FUNCTION('WEEK', r.createDate) = FUNCTION('WEEK', CURRENT_DATE) AND FUNCTION('YEAR', r.createDate) = FUNCTION('YEAR' , CURRENT_DATE)")
    Long countGlobalReservationByWeek();

    // nombre de reservation de ce mois
    @Query("SELECT COUNT(r) FROM Reservation r WHERE FUNCTION('MONTH', r.createDate) = FUNCTION('MONTH', CURRENT_DATE) AND FUNCTION('YEAR', r.createDate) = FUNCTION('YEAR', CURRENT_DATE)")
    long countGlobalReservationsThisMonth();

    // nombre de reservation de cette année
    @Query("SELECT COUNT(r) FROM Reservation r WHERE FUNCTION('YEAR', r.createDate) = FUNCTION('YEAR', CURRENT_DATE)")
    long countGlobalReservationsThisYear();

    // nombre de reservation annulée
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.annulee = true AND FUNCTION('DATE', r.createDate) = FUNCTION('CURRENT_DATE')")
    long countGlobalCancelledReservationsToday();

}
