package com.example.izibus.controllers;

import com.example.izibus.dto.TrajetDto;
import com.example.izibus.services.TrajetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trajets")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping("/ajouter")
    public ResponseEntity<TrajetDto> ajouterTrajet(@RequestBody TrajetDto trajetDto) {
        TrajetDto nouveauTrajet = trajetService.ajouterTrajet(trajetDto);
        return ResponseEntity.ok(nouveauTrajet);
    }

    @GetMapping("/tous")
    public ResponseEntity<List<TrajetDto>> recupererTousLesTrajets() {
        List<TrajetDto> trajets = trajetService.recupererTousLesTrajets();
        return ResponseEntity.ok(trajets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrajetDto> recupererTrajetParId(@PathVariable Long id) {
        TrajetDto trajet = trajetService.recupererTrajetParId(id);
        return ResponseEntity.ok(trajet);
    }

    @PutMapping("/modifier/{id}")
    public ResponseEntity<TrajetDto> modifierTrajet(@PathVariable Long id, @RequestBody TrajetDto trajetDto) {
        TrajetDto trajetModifie = trajetService.modifierTrajet(id, trajetDto);
        return ResponseEntity.ok(trajetModifie);
    }

    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<String> supprimerTrajet(@PathVariable Long id) {
        trajetService.supprimerTrajet(id);
        return ResponseEntity.ok("Trajet supprimé avec succès !");
    }

    @GetMapping("/compagnie/{id}")
    public ResponseEntity<List<TrajetDto>> recupererTousLesTrajetsParCompagnie(@PathVariable Long id) {
        List<TrajetDto> trajets = trajetService.recupererTousLesTrajetsParCompagnie(id);
        return ResponseEntity.ok(trajets);
    }

    @GetMapping("/recherche/depart")
    public ResponseEntity<List<TrajetDto>> rechercherTrajetsParLieuDepart(@RequestParam String lieuDepart) {
        List<TrajetDto> trajets = trajetService.rechercherTrajetsParLieuDepart(lieuDepart);
        return ResponseEntity.ok(trajets);
    }

    @GetMapping("/recherche/arriver")
    public ResponseEntity<List<TrajetDto>> rechercherTrajetsParLieuArriver(@RequestParam String lieuArriver) {
        List<TrajetDto> trajets = trajetService.rechercherTrajetsParLieuArriver(lieuArriver);
        return ResponseEntity.ok(trajets);
    }

    @GetMapping("/recherche/prix")
    public ResponseEntity<List<TrajetDto>> rechercherTrajetsParPrixMax(@RequestParam double prixMax) {
        List<TrajetDto> trajets = trajetService.rechercherTrajetsParPrixMax(prixMax);
        return ResponseEntity.ok(trajets);
    }
}
