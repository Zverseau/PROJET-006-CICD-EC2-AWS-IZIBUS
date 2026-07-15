package com.example.izibus.services;

import com.example.izibus.dto.AdministrateurDTO;
import com.example.izibus.entities.Administrateur;
import com.example.izibus.mappers.AdministrateurMapper;
import com.example.izibus.repositories.AdministrateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdministrateurService {

    private final AdministrateurRepository adminRepository;
    private final AdministrateurMapper adminMapper;

    public AdministrateurService(AdministrateurRepository adminRepository,
                                 AdministrateurMapper adminMapper) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
    }

    public AdministrateurDTO getAdminById(Long id) {
        Administrateur admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Administrateur non trouvé"));
        return adminMapper.toDto(admin);
    }

    public AdministrateurDTO getAdminByEmail(String email) {
        Administrateur admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Administrateur non trouvé"));
        return adminMapper.toDto(admin);
    }

    public List<AdministrateurDTO> getAdminsByNom(String nom) {
        List<Administrateur> admins = adminRepository.findByNomAdminContainingIgnoreCase(nom);
        return admins.stream()
                .map(adminMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<AdministrateurDTO> getAllAdmins() {
        List<Administrateur> admins = adminRepository.findAll();
        return admins.stream()
                .map(adminMapper::toDto)
                .collect(Collectors.toList());
    }

    public AdministrateurDTO updateAdmin(Long id, AdministrateurDTO adminDTO) {
        Administrateur existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Administrateur non trouvé"));

        // Mise à jour manuelle des champs
        existingAdmin.setEmail(adminDTO.getEmail());
        existingAdmin.setNomAdmin(adminDTO.getNomAdmin());
        existingAdmin.setPrenomAdmin(adminDTO.getPrenomAdmin());

        Administrateur updatedAdmin = adminRepository.save(existingAdmin);
        return adminMapper.toDto(updatedAdmin);
    }

    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new EntityNotFoundException("Administrateur non trouvé");
        }
        adminRepository.deleteById(id);
    }
}