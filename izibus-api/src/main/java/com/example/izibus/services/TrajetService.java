package com.example.izibus.services;

import com.example.izibus.dto.TrajetDto;
import com.example.izibus.entities.Compagnie;
import com.example.izibus.entities.Trajet;
import com.example.izibus.mappers.TrajetMapper;
import com.example.izibus.repositories.CompagnieRepository;
import com.example.izibus.repositories.TrajetRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrajetService {

    private final TrajetRepository trajetRepository;
    private final CompagnieRepository compagnieRepository;
    private final TrajetMapper trajetMapper;

    public TrajetService(
            TrajetRepository trajetRepository,
            CompagnieRepository compagnieRepository,
            TrajetMapper trajetMapper
    ) {
        this.trajetRepository = trajetRepository;
        this.compagnieRepository = compagnieRepository;
        this.trajetMapper = trajetMapper;
    }

    // methode pour ajouter un nouveau trajet
    public TrajetDto ajouterTrajet(TrajetDto trajetDto) {
        Compagnie compagnie = compagnieRepository.findById(trajetDto.getCompagnieId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnie introuvable avec l'ID : " + trajetDto.getCompagnieId()));

        Trajet trajet = trajetMapper.toEntity(trajetDto);
        trajet.setCompagnie(compagnie);

        trajet = trajetRepository.save(trajet);
        return trajetMapper.toDto(trajet);
    }



    // methode pour recuperer tous les trajets
    public List<TrajetDto> recupererTousLesTrajets() {
        return trajetRepository.findAll().stream()
                .map(trajetMapper::toDto)
                .collect(Collectors.toList());
    }

    // methode pour recuperer un trajet par son id
    public TrajetDto recupererTrajetParId(Long id) {
        Trajet trajet = trajetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trajet introuvable avec l'ID : " + id));
        return trajetMapper.toDto(trajet);
    }


    // methode pour recuperer les trajets crée par une compagnie donnée
    public List<TrajetDto> recupererTousLesTrajetsParCompagnie(Long id) {
        if (!compagnieRepository.existsById(id)) {
            throw new IllegalArgumentException("Compagnie introuvable avec l'ID : " + id);
        }

        return trajetRepository.findByCompagnie_Id(id).stream()
                .map(trajet -> {
                    TrajetDto dto = trajetMapper.toDto(trajet);
                    // Ajout manuel du nom de la compagnie
                    dto.setNomCompagnie(trajet.getCompagnie().getNomCompagnie());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Modifie un trajet existant.
     * @param id L'ID du trajet à modifier.
     * @param trajetDto Les nouvelles données du trajet.
     * @return Le trajet modifié sous forme de DTO.
     */
    public TrajetDto modifierTrajet(Long id, TrajetDto trajetDto) {
        Trajet trajetExistant = trajetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trajet introuvable avec l'ID : " + id));

        // Récupération de la compagnie existante au lieu de la remplacer
        Compagnie compagnie = trajetExistant.getCompagnie();

        // Mise à jour uniquement des champs modifiables
        trajetExistant.setNombrePlaces(trajetDto.getNombrePlaces());
        trajetExistant.setHeureDepart(trajetDto.getHeureDepart());
        trajetExistant.setHeureArriver(trajetDto.getHeureArriver());
        trajetExistant.setLieuDepart(trajetDto.getLieuDepart());
        trajetExistant.setLieuArriver(trajetDto.getLieuArriver());
        trajetExistant.setPrix(trajetDto.getPrix());
        trajetExistant.setDate(trajetDto.getDate()); // Ajout de la date

        trajetExistant = trajetRepository.save(trajetExistant);
        return trajetMapper.toDto(trajetExistant);
    }


    // methode pour suprimer un trjat
    public void supprimerTrajet(Long id) {
        if (!trajetRepository.existsById(id)) {
            throw new IllegalArgumentException("Trajet introuvable avec l'ID : " + id);
        }
        trajetRepository.deleteById(id);
    }


    // methode pour rechercher les trajets par id d'une compagnie
    public List<TrajetDto> rechercherTrajetsParCompagnie(Long id) {
        return trajetRepository.findByCompagnie_Id(id).stream()
                .map(trajetMapper::toDto)
                .collect(Collectors.toList());
    }

    // methode pour rechercher les trajets par lieu de depart
    public List<TrajetDto> rechercherTrajetsParLieuDepart(String lieuDepart) {
        return trajetRepository.findByLieuDepart(lieuDepart).stream()
                .map(trajetMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TrajetDto> rechercherTrajetsParLieuArriver(String lieuArriver) {
        return trajetRepository.findByLieuArriver(lieuArriver).stream()
                .map(trajetMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TrajetDto> rechercherTrajetsParPrixMax(double prixMax) {
        return trajetRepository.findByPrixLessThanEqual(prixMax).stream()
                .map(trajetMapper::toDto)
                .collect(Collectors.toList());
    }

}
