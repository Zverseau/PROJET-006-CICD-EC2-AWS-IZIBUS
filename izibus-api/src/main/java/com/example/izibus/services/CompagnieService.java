package com.example.izibus.services;

import com.example.izibus.dto.CompagnieResponse;
import com.example.izibus.dto.CompagnieUpdateRequest;
import com.example.izibus.entities.Compagnie;
import com.example.izibus.exceptions.CompagnieNotFoundException;
import com.example.izibus.mappers.CompagnieMapper;
import com.example.izibus.repositories.CompagnieRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompagnieService {
    private final CompagnieRepository compagnieRepository;
    private final CompagnieMapper compagnieMapper;

    public CompagnieService(CompagnieRepository compagnieRepository, CompagnieMapper compagnieMapper) {
        this.compagnieRepository = compagnieRepository;
        this.compagnieMapper = compagnieMapper;
    }

    public List<CompagnieResponse> getAllCompagnies() {
        return compagnieRepository.findAll().stream()
                .map(compagnieMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CompagnieResponse getCompagnieById(Long id) {
        return compagnieRepository.findById(id)
                .map(compagnieMapper::toResponse)
                .orElseThrow(() -> new CompagnieNotFoundException("Compagnie non trouvée"));
    }

    public CompagnieResponse getCompagnieByNom(String nom) {
        return compagnieRepository.findByNomCompagnie(nom)
                .map(compagnieMapper::toResponse)
                .orElseThrow(() -> new CompagnieNotFoundException("Compagnie non trouvée"));
    }

    public CompagnieResponse getCompagnieByEmail(String email) {
        return compagnieRepository.findByEmail(email)
                .map(compagnieMapper::toResponse)
                .orElseThrow(() -> new CompagnieNotFoundException("Compagnie non trouvée"));
    }

    public CompagnieResponse updateCompagnie(Long id, CompagnieUpdateRequest request) {
        Compagnie compagnie = compagnieRepository.findById(id)
                .orElseThrow(() -> new CompagnieNotFoundException("Compagnie non trouvée"));

        // Mise à jour partielle
        if (request.getNomCompagnie() != null) compagnie.setNomCompagnie(request.getNomCompagnie());
        if (request.getDescription() != null) compagnie.setDescription(request.getDescription());
        if (request.getTelephoneCompagnie() != null) compagnie.setTelephoneCompagnie(request.getTelephoneCompagnie());
        if (request.getLogoCompagnie() != null) compagnie.setLogoCompagnie(request.getLogoCompagnie());

        return compagnieMapper.toResponse(compagnieRepository.save(compagnie));
    }

    public void deleteCompagnie(Long id) {
        compagnieRepository.deleteById(id);
    }
}