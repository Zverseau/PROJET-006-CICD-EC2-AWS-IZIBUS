package com.example.izibus.services;

import com.example.izibus.dto.*;
import com.example.izibus.entities.Client;
import com.example.izibus.entities.Compagnie;
import com.example.izibus.entities.Reservation;
import com.example.izibus.entities.Trajet;
import com.example.izibus.mappers.ClientMapper;
import com.example.izibus.mappers.ReservationMapper;
import com.example.izibus.repositories.ClientRepository;
import com.example.izibus.repositories.CompagnieRepository;
import com.example.izibus.repositories.ReservationRepository;
import com.example.izibus.repositories.TrajetRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.LocalTime;

/**
 * Service pour la gestion des réservations.
 */
@Service
@Transactional
public class ReservationService {

    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;
    private final CompagnieRepository compagnieRepository;
    private final ClientRepository clientRepository;
    private final TrajetRepository trajetRepository;
    private final PaiementService paiementService;
    private final ClientMapper clientMapper;


    public ReservationService(
            ReservationMapper reservationMapper,
            ReservationRepository reservationRepository,
            CompagnieRepository compagnieRepository,
            ClientRepository clientRepository,
            ClientMapper clientMapper,
            TrajetRepository trajetRepository, PaiementService paiementService
    ) {
        this.reservationMapper = reservationMapper;
        this.reservationRepository = reservationRepository;
        this.compagnieRepository = compagnieRepository;
        this.clientMapper          = clientMapper;
        this.clientRepository = clientRepository;
        this.trajetRepository = trajetRepository;
        this.paiementService = paiementService;
    }




    public ReservationDto ajouterReservation(ReservationDto reservationDto) {
        Client client = clientRepository.findById(reservationDto.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable avec l'ID : " + reservationDto.getClientId()));

        Trajet trajet = trajetRepository.findById(reservationDto.getTrajetId())
                .orElseThrow(() -> new IllegalArgumentException("Trajet introuvable avec l'ID : " + reservationDto.getTrajetId()));

        Compagnie compagnie = trajet.getCompagnie(); // ✅ Corrigé ici

        if (compagnie == null) {
            throw new IllegalArgumentException("Aucune compagnie associée à ce trajet");
        }


        if (reservationDto.getNombrePlacesReservees() > trajet.getNombrePlaces()) {
            throw new IllegalArgumentException("Nombre de places demandées supérieur aux places disponibles !");
        }

        double montantTotal = reservationDto.getNombrePlacesReservees() * trajet.getPrix();

        trajet.setNombrePlaces(trajet.getNombrePlaces() - reservationDto.getNombrePlacesReservees());
        trajetRepository.save(trajet);

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setTrajet(trajet);
        reservation.setCompagnie(compagnie);
        reservation.setNomPassager(reservationDto.getNomPassager());
        reservation.setPrenomPassager(reservationDto.getPrenomPassager());
        reservation.setTelephonePassager(reservationDto.getTelephonePassager());
        reservation.setNombrePlacesReservees(reservationDto.getNombrePlacesReservees());
        reservation.setMontantTotal(montantTotal);

        reservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }


    public ReservationDto reserverPayer(ReserverPayerDto reserverPayerDto) {
        // Étape 1 : Ajouter la réservation
        ReservationDto reservationDto = ajouterReservation(reserverPayerDto.getReservation());

        // Étape 2 : Créer le PaiementDto à partir de la réservation et du mode de paiement fourni
        PaiementDto paiementDto = new PaiementDto();
        paiementDto.setReservationId(reservationDto.getId());
        paiementDto.setModePaiement(reserverPayerDto.getModePaiement());
        paiementDto.setMontant(reservationDto.getMontantTotal());

        // Étape 3 : Enregistrer le paiement
        paiementService.effectuerPaiement(paiementDto);
        // Étape 4 : Retourner la réservation confirmée
        return reservationDto;
    }



    /**
     * La compagnie connectée réserve un trajet pour un passager non‐enregistré.
     * @param compagnieId issu du token JWT
     * @param dto contient nomPassager, prenomPassager, telephonePassager, trajetId, nombrePlacesReservees
     */
    public ReservationDto reserverPourClient(Long compagnieId, ReservationDto dto) {
        // 1. On vérifie que la compagnie existe
        Compagnie compagnie = compagnieRepository.findById(compagnieId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Compagnie introuvable avec l'ID : " + compagnieId));

        // 2. On récupère le trajet
        Trajet trajet = trajetRepository.findById(dto.getTrajetId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trajet introuvable avec l'ID : " + dto.getTrajetId()));

        // 3. On vérifie la disponibilité
        if (dto.getNombrePlacesReservees() > trajet.getNombrePlaces()) {
            throw new IllegalArgumentException(
                    "Nombre de places demandé supérieur aux places disponibles !");
        }

        // 4. On calcule le montant et on met à jour le stock
        double montantTotal = dto.getNombrePlacesReservees() * trajet.getPrix();
        trajet.setNombrePlaces(trajet.getNombrePlaces() - dto.getNombrePlacesReservees());
        trajetRepository.save(trajet);

        // **Ici**, on récupère ou crée le client fantôme :
        Client clientFantome = getClientFantome();

        // 5. On crée la réservation (sans client en base)
        Reservation reservation = new Reservation();
        reservation.setClient(clientFantome);
        reservation.setCompagnie(compagnie);
        reservation.setTrajet(trajet);
        reservation.setNomPassager(dto.getNomPassager());
        reservation.setPrenomPassager(dto.getPrenomPassager());
        reservation.setTelephonePassager(dto.getTelephonePassager());
        reservation.setNombrePlacesReservees(dto.getNombrePlacesReservees());
        reservation.setMontantTotal(montantTotal);
        reservation.setAnnulee(false);

        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toDto(saved);
    }

    /**
     * Récupère un client générique (numéro spécial), ou le crée s’il n'existe pas.
     */
    private Client getClientFantome() {
        String numeroFantome = "000000000";
        return clientRepository.findByTelephoneClient(numeroFantome)
                .orElseGet(() -> {
                    Client fantome = new Client();
                    fantome.setTelephoneClient(numeroFantome);
                    fantome.setNomClient("Invité");
                    fantome.setPrenomClient("");
                    fantome.setVerified(false);
                    return clientRepository.save(fantome);
                });
    }


    /**
     * Récupère toutes les réservations de la compagnie connectée.
     */
    public List<ReservationDto> recupererReservationsParCompagnie(Long compagnieId) {
        if (!compagnieRepository.existsById(compagnieId)) {
            throw new IllegalArgumentException("Compagnie introuvable avec l'ID : " + compagnieId);
        }
        return reservationRepository.findByCompagnie_Id(compagnieId).stream()
                .map(res -> {
                    ReservationDto dto = reservationMapper.toDto(res);
                    // Ajout manuel du nom de la compagnie
                    dto.setNomCompagnie(res.getCompagnie().getNomCompagnie());

                    // Trajet
                    Trajet t = res.getTrajet();
                    dto.setLieuDepart(t.getLieuDepart());
                    dto.setLieuArriver(t.getLieuArriver());
                    dto.setDateTrajet(t.getDate());

                    // Gestion du cas où createDate est null
                    if (res.getCreateDate() != null) {
                        dto.setDateReservation(res.getCreateDate().atStartOfDay());
                    } else {
                        // Valeur par défaut (date actuelle ou autre)
                        dto.setDateReservation(LocalDate.now().atStartOfDay());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    /**
     * Récupère tous les clients **existants** qui ont au moins une
     * réservation pour cette compagnie.
     */
    public List<ClientDto> recupererClientsParCompagnie(Long compagnieId) {
        if (!compagnieRepository.existsById(compagnieId)) {
            throw new IllegalArgumentException(
                    "Compagnie introuvable avec l'ID : " + compagnieId);
        }
        return reservationRepository.findByCompagnie_Id(compagnieId).stream()
                .map(Reservation::getClient)          // récupère le Client, null si non lié
                .filter(Objects::nonNull)             // on garde seulement les clients en base
                // on retire le client “fantôme” en filtrant son numéro
                .filter(c -> !"000000000".equals(c.getTelephoneClient()))
                .distinct()                           // un client = une seule fois
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }


    // methode pour recuperer la liste des reservation faite par des clients existant pour la compagnie connectée
    public List<ReservationDto> recupererReservationsClientsParCompagnie(Long compagnieId) {
        if (!compagnieRepository.existsById(compagnieId)) {
            throw new IllegalArgumentException("Compagnie introuvable avec l'ID : " + compagnieId);
        }

        return reservationRepository.findByCompagnie_Id(compagnieId).stream()
                .filter(res -> !"000000000".equals(res.getClient().getTelephoneClient()))
                .map(res -> {
                    ReservationDto dto = reservationMapper.toDto(res);
                    // Client
                    Client c = res.getClient();
                    dto.setNomClient(c.getNomClient());
                    dto.setPrenomClient(c.getPrenomClient());
                    dto.setTelephoneClient(c.getTelephoneClient());
                    // Trajet
                    Trajet t = res.getTrajet();
                    dto.setLieuDepart(t.getLieuDepart());
                    dto.setLieuArriver(t.getLieuArriver());
                    dto.setDateTrajet(t.getDate());

                    // AJOUTEZ CE BLOC POUR LA DATE DE RÉSERVATION
                    if (res.getCreateDate() != null) {
                        dto.setDateReservation(res.getCreateDate().atStartOfDay());
                    } else {
                        dto.setDateReservation(LocalDate.now().atStartOfDay());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Modifie une réservation existante pour la compagnie connectée.
     * @param compagnieId issu du token JWT
     * @param reservationId ID de la réservation à modifier
     * @param dto contient nomPassager, prenomPassager, telephonePassager, nombrePlacesReservees
     */
    public ReservationDto modifierReservation(
            Long compagnieId,
            Long reservationId,
            ReservationDto dto
    ) {
        // 1. Vérifier la compagnie
        if (!compagnieRepository.existsById(compagnieId)) {
            throw new IllegalArgumentException("Compagnie introuvable avec l'ID : " + compagnieId);
        }

        // 2. Charger la réservation
        Reservation existing = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Réservation introuvable avec l'ID : " + reservationId));

        // 3. S'assurer que c'est bien la même compagnie
        if (!Objects.equals(existing.getCompagnie().getId(), compagnieId)) {
            throw new IllegalArgumentException("Vous n’êtes pas autorisé à modifier cette réservation");
        }

        // 4. Ajuster le nombre de places (restauration de l’ancienne puis retrait de la nouvelle)
        Trajet trajet = existing.getTrajet();
        int delta = dto.getNombrePlacesReservees() - existing.getNombrePlacesReservees();
        if (delta > trajet.getNombrePlaces()) {
            throw new IllegalArgumentException("Pas assez de places disponibles pour ce trajet");
        }
        trajet.setNombrePlaces(trajet.getNombrePlaces() - delta);
        trajetRepository.save(trajet);

        // 5. Mettre à jour les champs modifiables
        existing.setNomPassager(dto.getNomPassager());
        existing.setPrenomPassager(dto.getPrenomPassager());
        existing.setTelephonePassager(dto.getTelephonePassager());
        existing.setNombrePlacesReservees(dto.getNombrePlacesReservees());
        existing.setMontantTotal(dto.getNombrePlacesReservees() * trajet.getPrix());

        // 6. Sauvegarder
        Reservation saved = reservationRepository.save(existing);
        return reservationMapper.toDto(saved);
    }





    public List<ReservationDto> recupererToutesLesReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReservationDto recupererReservationParId(Long id) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable avec l'ID : " + id));

        ReservationDto dto = reservationMapper.toDto(res);
        Trajet t = res.getTrajet();

        dto.setTrajetId(t.getId());
        dto.setLieuDepart(t.getLieuDepart());
        dto.setLieuArriver(t.getLieuArriver());
        dto.setDateTrajet(t.getDate());

        if (res.getCreateDate() != null) {
            dto.setDateReservation(res.getCreateDate().atStartOfDay());
        }

        // Conversion sécurisée des heures
        dto.setHeureDepart(convertToLocalTime(t.getHeureDepart()));
        dto.setHeureArriver(convertToLocalTime(t.getHeureArriver()));

        if (res.getCompagnie() != null) {
            dto.setNomCompagnie(res.getCompagnie().getNomCompagnie());
        }

        return dto;
    }

    // Méthode de conversion robuste
    private LocalTime convertToLocalTime(Object timeObj) {
        if (timeObj == null) return null;

        if (timeObj instanceof LocalTime) {
            return (LocalTime) timeObj;
        }
        else if (timeObj instanceof java.sql.Time) {
            return ((java.sql.Time) timeObj).toLocalTime();
        }
        else if (timeObj instanceof java.util.Date) {
            return ((java.util.Date) timeObj).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime();
        }

        return null;
    }


    public List<ReservationDto> recupererReservationsParClient(Long id) {
        return reservationRepository.findByClient_Id(id).stream()
                .map(res -> {
                    ReservationDto dto = reservationMapper.toDto(res);

                    // Remplir les informations du trajet
                    Trajet t = res.getTrajet();
                    dto.setLieuDepart(t.getLieuDepart());
                    dto.setLieuArriver(t.getLieuArriver());
                    dto.setDateTrajet(t.getDate());

                    // Remplir le nom de la compagnie
                    dto.setNomCompagnie(t.getCompagnie().getNomCompagnie());

                    // Remplir les informations du client
                    Client c = res.getClient();
                    dto.setNomClient(c.getNomClient());
                    dto.setPrenomClient(c.getPrenomClient());
                    dto.setTelephoneClient(c.getTelephoneClient());

                    // Date de réservation
                    if (res.getCreateDate() != null) {
                        dto.setDateReservation(res.getCreateDate().atStartOfDay());
                    } else {
                        dto.setDateReservation(LocalDate.now().atStartOfDay());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<ReservationDto> recupererReservationsParTrajet(Long id) {
        return reservationRepository.findByTrajet_Id(id).stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public void supprimerReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable avec l'ID : " + id));

        Trajet trajet = reservation.getTrajet();
        trajet.setNombrePlaces(trajet.getNombrePlaces() + reservation.getNombrePlacesReservees());
        trajetRepository.save(trajet);

        reservationRepository.deleteById(id);
    }




    // methode pour recuperer les statistiques de la compagnie connectée
    public ReservationStatDto getStatsForCompagnie(Long compagnieId) {
        ReservationStatDto stats = new ReservationStatDto();
        stats.setToday(reservationRepository.countReservationByDay(compagnieId));
        stats.setWeek(reservationRepository.countReservationByWeek(compagnieId));
        stats.setMonth(reservationRepository.countReservationsThisMonth(compagnieId));
        stats.setYear(reservationRepository.countReservationsThisYear(compagnieId));
        stats.setCancelledToday(reservationRepository.countCancelledReservationsToday(compagnieId));
        return stats;
    }

    // Méthodes pour les statistiques globales
    public long getTodayReservationCount() {
        return reservationRepository.countGlobalReservationByDay();
    }

    public long getWeekReservationCount() {
        return reservationRepository.countGlobalReservationByWeek();
    }

    public long getMonthReservationCount() {
        return reservationRepository.countGlobalReservationsThisMonth();
    }

    public long getYearReservationCount() {
        return reservationRepository.countGlobalReservationsThisYear();
    }

    public long getCancelledTodayReservationCount() {
        return reservationRepository.countGlobalCancelledReservationsToday();
    }

    // Méthode pour récupérer toutes les stats globales
    public ReservationStatDto getGlobalStats() {
        ReservationStatDto stats = new ReservationStatDto();
        stats.setToday(getTodayReservationCount());
        stats.setWeek(getWeekReservationCount());
        stats.setMonth(getMonthReservationCount());
        stats.setYear(getYearReservationCount());
        stats.setCancelledToday(getCancelledTodayReservationCount());
        return stats;
    }

}
