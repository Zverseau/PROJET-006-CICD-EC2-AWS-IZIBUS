package com.example.izibus.controllers;

import com.example.izibus.dto.ClientDto;
import com.example.izibus.dto.ReservationDto;
import com.example.izibus.dto.ReservationStatDto;
import com.example.izibus.dto.ReserverPayerDto;
import com.example.izibus.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import com.example.izibus.services.PdfGeneratorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final PdfGeneratorService pdfGeneratorService;

    public ReservationController(ReservationService reservationService, PdfGeneratorService pdfGeneratorService) {
        this.reservationService = reservationService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @PostMapping("/reserver-payer")
    public ReservationDto reserverEtPayer(@RequestBody ReserverPayerDto reserverPayerDto) {
        System.out.println("Reçu : " + reserverPayerDto);
        System.out.println("Client ID : " + reserverPayerDto.getReservation().getClientId());
        return reservationService.reserverPayer(reserverPayerDto);

    }

    @PostMapping("/ajouter")
    public ResponseEntity<ReservationDto> ajouterReservation(@RequestBody ReservationDto reservationDto) {
        ReservationDto nouvelleReservation = reservationService.ajouterReservation(reservationDto);
        return ResponseEntity.ok(nouvelleReservation);
    }

    /**
     * POST /api/v1/reservations/compagnie/{id}/compagnie-reserve
     * Réservation par la compagnie connectée pour un passager non-enregistré.
     */
    @PostMapping("/compagnie/{id}/compagnie-reserve")
    public ResponseEntity<ReservationDto> reserverPourClient(
            @PathVariable("id") Long compagnieId,
            @RequestBody ReservationDto reservationDto) {
        ReservationDto saved = reservationService.reserverPourClient(compagnieId, reservationDto);
        return ResponseEntity.ok(saved);
    }

    /**
     * GET /api/v1/reservations/compagnie/{id}/mes-reservations
     */
    @GetMapping("/compagnie/{id}/mes-reservations")
    public ResponseEntity<List<ReservationDto>> getMesReservations(
            @PathVariable("id") Long compagnieId) {
        List<ReservationDto> list = reservationService.recupererReservationsParCompagnie(compagnieId);
        return ResponseEntity.ok(list);
    }

    //endpoint pour recuperer les reservations faites par les client existant
    @GetMapping("/compagnie/{id}/reservations-clients")
    public ResponseEntity<List<ReservationDto>> getReservationsClientsParCompagnie(
            @PathVariable("id") Long compagnieId) {
        List<ReservationDto> list = reservationService.recupererReservationsClientsParCompagnie(compagnieId);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/v1/reservations/compagnie/{id}/clients
     */
    @GetMapping("/compagnie/{id}/clients")
    public ResponseEntity<List<ClientDto>> getClientsParCompagnie(
            @PathVariable("id") Long compagnieId) {
        List<ClientDto> clients = reservationService.recupererClientsParCompagnie(compagnieId);
        return ResponseEntity.ok(clients);
    }

    /**
     * PUT /api/v1/reservations/compagnie/{id}/modifier/{reservationId}
     * Permet à la compagnie connectée de modifier une de ses réservations.
     */
    @PutMapping("/compagnie/{id}/modifier/{reservationId}")
    public ResponseEntity<ReservationDto> modifierReservation(
            @PathVariable("id") Long compagnieId,
            @PathVariable("reservationId") Long reservationId,
            @RequestBody ReservationDto dto
    ) {
        ReservationDto updated = reservationService.modifierReservation(compagnieId, reservationId, dto);
        return ResponseEntity.ok(updated);
    }



    @GetMapping("/toutes")
    public ResponseEntity<List<ReservationDto>> recupererToutesLesReservations() {
        List<ReservationDto> reservations = reservationService.recupererToutesLesReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> recupererReservationParId(@PathVariable Long id) {
        ReservationDto reservation = reservationService.recupererReservationParId(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<List<ReservationDto>> recupererReservationsParClient(@PathVariable Long id) {
        List<ReservationDto> reservations = reservationService.recupererReservationsParClient(id);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/trajet/{id}")
    public ResponseEntity<List<ReservationDto>> recupererReservationsParTrajet(@PathVariable Long id) {
        List<ReservationDto> reservations = reservationService.recupererReservationsParTrajet(id);
        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<String> supprimerReservation(@PathVariable Long id) {
        reservationService.supprimerReservation(id);
        return ResponseEntity.ok("Réservation supprimée avec succès !");
    }


    // endpoint pour les statistiques de la compagnie connectée
    @GetMapping("/compagnie/{id}/statistiques")
    public ResponseEntity<ReservationStatDto> getStatsForCompagnie(
            @PathVariable("id") Long compagnieId) {
        ReservationStatDto stats = reservationService.getStatsForCompagnie(compagnieId);
        return ResponseEntity.ok(stats);
    }


    // Endpoints individuels
    @GetMapping("/statistiques/jour")
    public ResponseEntity<Long> getReservationsDuJour() {
        return ResponseEntity.ok(reservationService.getTodayReservationCount());
    }

    @GetMapping("/statistiques/semaine")
    public ResponseEntity<Long> getReservationsDeLaSemaine() {
        return ResponseEntity.ok(reservationService.getWeekReservationCount());
    }

    @GetMapping("/statistiques/mois")
    public ResponseEntity<Long> getReservationsDuMois() {
        return ResponseEntity.ok(reservationService.getMonthReservationCount());
    }

    @GetMapping("/statistiques/annee")
    public ResponseEntity<Long> getReservationsDeLAnnee() {
        return ResponseEntity.ok(reservationService.getYearReservationCount());
    }

    @GetMapping("/statistiques/annulees-aujourd-hui")
    public ResponseEntity<Long> getReservationsAnnuleesAujourdhui() {
        return ResponseEntity.ok(reservationService.getCancelledTodayReservationCount());
    }

    // Endpoint pour toutes les stats globales
    @GetMapping("/statistiques/global")
    public ResponseEntity<ReservationStatDto> getGlobalStats() {
        return ResponseEntity.ok(reservationService.getGlobalStats());
    }

    @GetMapping("/{id}/ticket")
    public ResponseEntity<Resource> genererTicketReservation(@PathVariable Long id) {
        try {
            byte[] pdfBytes = pdfGeneratorService.generateTicketForReservation(id);
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket_reservation_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
