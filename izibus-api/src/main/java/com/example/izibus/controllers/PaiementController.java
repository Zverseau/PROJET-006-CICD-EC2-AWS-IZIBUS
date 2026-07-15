package com.example.izibus.controllers;

import com.example.izibus.dto.PaiementDto;
import com.example.izibus.dto.PaiementStatDto;
import com.example.izibus.services.PaiementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @PostMapping("/effectuer")
    public ResponseEntity<PaiementDto> effectuerPaiement(@RequestBody PaiementDto paiementDto) {
        PaiementDto paiementEnregistre = paiementService.effectuerPaiement(paiementDto);
        return ResponseEntity.ok(paiementEnregistre);
    }

    @GetMapping("/historique")
    public ResponseEntity<List<PaiementDto>> getHistoriquePaiements() {
        return ResponseEntity.ok(paiementService.getHistoriquePaiements());
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<List<PaiementDto>> getPaiementsParReservation(@PathVariable Long id) {
        return ResponseEntity.ok(paiementService.getPaiementsParReservation(id));
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<List<PaiementDto>> getPaiementsParClient(@PathVariable Long id) {
        return ResponseEntity.ok(paiementService.getPaiementsParClient(id));
    }

    @GetMapping("/byDay")
    public List<PaiementStatDto> getChiffreAffaireParJour() {
        return paiementService.getChiffreAffaireByDay();
    }

    @GetMapping("/byMonth")
    public List<PaiementStatDto> getChiffreAffaireParMois() {
        return paiementService.getChiffreAffaireByMonth();
    }

    @GetMapping("/byYear")
    public List<PaiementStatDto> getChiffreAffaireParAnnee() {
        return paiementService.getChiffreAffaireByYear();
    }

    // endpoint pour les chiffres d'affaire d'une compagnie connectée
    @GetMapping("/compagnie/{compagnieId}/stats")
    public ResponseEntity<Map<String, Double>> getChiffreAffaireStatsByCompagnie(@PathVariable Long compagnieId) {
        return ResponseEntity.ok(paiementService.getChiffreAffaireStatsByCompagnie(compagnieId));
    }

}
