package com.example.izibus.controllers;

import com.example.izibus.dto.AdministrateurDTO;
import com.example.izibus.services.AdministrateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdministrateurController {

    private final AdministrateurService adminService;

    public AdministrateurController(AdministrateurService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministrateurDTO> getAdminById(@PathVariable Long id) {
        AdministrateurDTO admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministrateurDTO> getAdminByEmail(@PathVariable String email) {
        AdministrateurDTO admin = adminService.getAdminByEmail(email);
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/nom/{nom}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdministrateurDTO>> getAdminsByNom(@PathVariable String nom) {
        List<AdministrateurDTO> admins = adminService.getAdminsByNom(nom);
        return ResponseEntity.ok(admins);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdministrateurDTO>> getAllAdmins() {
        List<AdministrateurDTO> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministrateurDTO> updateAdmin(
            @PathVariable Long id,
            @RequestBody AdministrateurDTO adminDTO) {
        AdministrateurDTO updatedAdmin = adminService.updateAdmin(id, adminDTO);
        return ResponseEntity.ok(updatedAdmin);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}