package com.example.izibus.services;

import com.example.izibus.dto.ClientDto;
import com.example.izibus.dto.ClientStatDto;
import com.example.izibus.entities.Client;
import com.example.izibus.mappers.ClientMapper;
import com.example.izibus.repositories.ClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private  final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    //Récupère tous les clients enregistrés.
    public List<ClientDto> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    //Récupère un client par son identifiant.
    public ClientDto getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec l'ID : " + id));
        return clientMapper.toDto(client);
    }

    //Récupère les clients par leur nom.
    public List<ClientDto> getClientsByNom(String nom) {
        return clientRepository.findAll().stream()
                .filter(client -> client.getNomClient().equalsIgnoreCase(nom))
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    //Récupère un client par son numéro de téléphone.
    public ClientDto getClientByTelephone(String telephone) {
        Client client = clientRepository.findByTelephoneClient(telephone)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec le téléphone : " + telephone));
        return clientMapper.toDto(client);
    }

    //Supprime un client par son ID.
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Client non trouvé avec l'ID : " + id);
        }
        clientRepository.deleteById(id);
    }

    //Methode pour obtenir le nombre de clients créés un jour donné
    public Long getClientByDay(LocalDate date){
        return clientRepository.countClientsByDay(date);
    }

    //Methode pour obtenir le nombre de clients crées dans un mois donné
    public Long getClientByMonth(int month, int year){
        return clientRepository.countClientsByMonth(month, year);
    }

    //Methode pour obtenir le nombre de clients créés dans une année donnée
    public Long getClientByYear(int year){
        return clientRepository.countClientsByYear(year);
    }


    // methode pour recuperer les stats globales des clients
    public Map<String, Object> getGlobalClientStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total clients
        stats.put("totalClients", clientRepository.count()); // Notez le "s" à totalClients

        // Stats temporelles
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        stats.put("today", getClientByDay(today));
        stats.put("thisMonth", getClientByMonth(month, year));
        stats.put("thisYear", getClientByYear(year));

        return stats;
    }

}
