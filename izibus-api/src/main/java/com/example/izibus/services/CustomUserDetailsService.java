package com.example.izibus.services;

import com.example.izibus.entities.UserBase;
import com.example.izibus.repositories.AdministrateurRepository;
import com.example.izibus.repositories.ClientRepository;
import com.example.izibus.repositories.CompagnieRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CompagnieRepository compagnieRepository;
    private final AdministrateurRepository administrateurRepository;

    public CustomUserDetailsService( CompagnieRepository compagnieRepository, AdministrateurRepository administrateurRepository) {
        this.compagnieRepository = compagnieRepository;
        this.administrateurRepository = administrateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserBase user = administrateurRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = compagnieRepository.findByEmail(email).orElse(null);
        }

        if (user == null) {
            System.out.println(" Utilisateur non trouvé : " + email);
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + email);
        }

        // Vérifie quel rôle est attribué
        System.out.println(" Chargement de l'utilisateur : " + user.getEmail() + " | Role: " + user.getRole().name());

        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())) // Ajout du préfixe ROLE_
        );
    }

}
