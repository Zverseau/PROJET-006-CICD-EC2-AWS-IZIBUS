package com.example.izibus.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        //  Autoriser toutes les origines (frontend dynamique)
        config.setAllowedOriginPatterns(Arrays.asList("*"));

        //  Autoriser toutes les méthodes HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        //  Autoriser tous les en-têtes nécessaires
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        //  Autoriser les informations d'identification (Cookies, JWT...)
        config.setAllowCredentials(true);

        // Associer la config à toutes les routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
