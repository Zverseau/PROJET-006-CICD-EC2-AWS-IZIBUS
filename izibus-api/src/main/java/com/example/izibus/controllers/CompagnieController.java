package com.example.izibus.controllers;

import com.example.izibus.dto.CompagnieResponse;
import com.example.izibus.dto.CompagnieUpdateRequest;
import com.example.izibus.services.CompagnieService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compagnies")
public class CompagnieController {
    private final CompagnieService compagnieService;

    public CompagnieController(CompagnieService compagnieService) {
        this.compagnieService = compagnieService;
    }

    @GetMapping
    public ResponseEntity<List<CompagnieResponse>> getAllCompagnies() {
        return ResponseEntity.ok(compagnieService.getAllCompagnies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompagnieResponse> getCompagnieById(@PathVariable Long id) {
        return ResponseEntity.ok(compagnieService.getCompagnieById(id));
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<CompagnieResponse> getCompagnieByName(@PathVariable String name) {
        return ResponseEntity.ok(compagnieService.getCompagnieByNom(name));
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<CompagnieResponse> getCompagnieByEmail(@PathVariable String email) {
        return ResponseEntity.ok(compagnieService.getCompagnieByEmail(email));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPAGNIE')")
    public ResponseEntity<CompagnieResponse> updateCompagnie(
            @PathVariable Long id,
            @RequestBody CompagnieUpdateRequest request
    ) {
        return ResponseEntity.ok(compagnieService.updateCompagnie(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompagnie(@PathVariable Long id) {
        compagnieService.deleteCompagnie(id);
        return ResponseEntity.noContent().build();
    }
}