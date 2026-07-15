package com.example.izibus.controllers;

import com.example.izibus.dto.ClientDto;
import com.example.izibus.dto.ClientStatDto;
import com.example.izibus.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private  final ClientService clientService;

    public ClientController(ClientService clientService  ) {
        this.clientService = clientService;
    }


    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<ClientDto> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        ClientDto client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<List<ClientDto>> getClientsByNom(@PathVariable String nom) {
        List<ClientDto> clients = clientService.getClientsByNom(nom);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/telephone/{telephone}")
    public ResponseEntity<ClientDto> getClientByTelephone(@PathVariable String telephone) {
        ClientDto client = clientService.getClientByTelephone(telephone);
        return ResponseEntity.ok(client);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok("Client supprimé avec succès");
    }

    @GetMapping("/client/byDay")
    public Long getClientByDay(@RequestParam("date") String date){
        LocalDate localDate = LocalDate.parse(date);
        return clientService.getClientByDay(localDate);
    }

    @GetMapping("/clients/byMonth")
    public Long getClientByMonth(@RequestParam("mois")  int mois, @RequestParam("annee") int annee){
        return clientService.getClientByMonth(mois, annee);
    }

    @GetMapping("/clients/byYear")
    public Long getClientsParAnnee(@RequestParam("annee") int annee) {
        return clientService.getClientByYear(annee);
    }

    // Nouvel endpoint pour les statistiques globales
    @GetMapping("/global-stats")
    public ResponseEntity<Map<String, Object>> getGlobalClientStats() {
        Map<String, Object> stats = clientService.getGlobalClientStats();
        return ResponseEntity.ok(stats);
    }

}
