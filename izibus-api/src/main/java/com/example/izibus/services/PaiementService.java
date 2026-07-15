package com.example.izibus.services;

import com.example.izibus.dto.PaiementDto;
import com.example.izibus.dto.PaiementStatDto;
import com.example.izibus.entities.Paiement;
import com.example.izibus.entities.Reservation;
import com.example.izibus.mappers.PaiementMapper;
import com.example.izibus.repositories.PaiementRepository;
import com.example.izibus.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour gérer les paiements et l'historique des transactions.
 */
@Service
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final PaiementMapper paiementMapper;

    public PaiementService(PaiementRepository paiementRepository, ReservationRepository reservationRepository, PaiementMapper paiementMapper) {
        this.paiementRepository = paiementRepository;
        this.reservationRepository = reservationRepository;
        this.paiementMapper = paiementMapper;
    }

    //Effectue un paiement pour une réservation donnée.
    public PaiementDto effectuerPaiement(PaiementDto paiementDto) {
        Reservation reservation = reservationRepository.findById(paiementDto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable avec l'ID : " + paiementDto.getReservationId()));

        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontant(reservation.getMontantTotal());
        paiement.setModePaiement(paiementDto.getModePaiement());
        paiement = paiementRepository.save(paiement);
        return paiementMapper.toDto(paiement);
    }

    // Récupère l'historique complet des paiements.
    public List<PaiementDto> getHistoriquePaiements() {
        return paiementRepository.findAll()
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

    // Récupère tous les paiements effectués pour une réservation spécifique.
    public List<PaiementDto> getPaiementsParReservation(Long id) {
        return paiementRepository.findByReservation_Id(id)
                .stream()
                .map(paiementMapper::toDto)
                .collect(Collectors.toList());
    }

   //Récupère tous les paiements effectués par un client donné.
   public List<PaiementDto> getPaiementsParClient(Long id) {
       return paiementRepository.findByReservation_Client_Id(id)
               .stream()
               .map(paiementMapper::toDto)
               .collect(Collectors.toList());
   }

    public List<PaiementStatDto> getChiffreAffaireByDay() {
        return paiementRepository.getChiffreAffaireByDay().stream()
                .map(obj -> new PaiementStatDto(obj[0].toString(), (Double) obj[1]))
                .collect(Collectors.toList());
    }

    public List<PaiementStatDto> getChiffreAffaireByMonth() {
        return paiementRepository.getChiffreAffaireByMonth().stream()
                .map(obj -> {
                    int annee = (Integer) obj[0];
                    int mois = (Integer) obj[1];
                    String periode = String.format("%04d-%02d", annee, mois);
                    return new PaiementStatDto(periode, (Double) obj[2]);
                })
                .collect(Collectors.toList());
    }

    public List<PaiementStatDto> getChiffreAffaireByYear() {
        return paiementRepository.getChiffreAffaireByYear().stream()
                .map(obj -> new PaiementStatDto(obj[0].toString(), (Double) obj[1]))
                .collect(Collectors.toList());
    }

    // Methode pour recuperer les chiffres d'affaire d'une compagnie connectée
    public Map<String, Double> getChiffreAffaireStatsByCompagnie(Long compagnieId) {
        Map<String, Double> stats = new HashMap<>();

        stats.put("aujourdhui", paiementRepository.getChiffreAffaireAujourdhuiByCompagnie(compagnieId));
        stats.put("cetteSemaine", paiementRepository.getChiffreAffaireCetteSemaineByCompagnie(compagnieId));
        stats.put("ceMois", paiementRepository.getChiffreAffaireCeMoisByCompagnie(compagnieId));
        stats.put("cetteAnnee", paiementRepository.getChiffreAffaireCetteAnneeByCompagnie(compagnieId));

        // Gérer les valeurs nulles
        for (String key : stats.keySet()) {
            if (stats.get(key) == null) {
                stats.put(key, 0.0);
            }
        }

        return stats;
    }


}
