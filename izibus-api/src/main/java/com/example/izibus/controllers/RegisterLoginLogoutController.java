package com.example.izibus.controllers;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.example.izibus.configuration.JwtUtils;
import com.example.izibus.configuration.VerificationCodeGenerator;
import com.example.izibus.dto.*;
import com.example.izibus.entities.*;
import com.example.izibus.mappers.RegisterMapper;
import com.example.izibus.repositories.AdministrateurRepository;
import com.example.izibus.repositories.ClientRepository;
import com.example.izibus.repositories.CompagnieRepository;
import com.example.izibus.services.sms.SmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/")
public class RegisterLoginLogoutController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterLoginLogoutController.class);
    private final AdministrateurRepository administrateurRepository;
    private final CompagnieRepository compagnieRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RegisterMapper registerMapper;
    private final JwtUtils jwtUtils;
    private final SmsService smsService;

    // MODIF: Changement du type de map pour stocker le code + expiration
    private final Map<String, OtpData> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, ResendAttempt> resendAttempts = new ConcurrentHashMap<>();

    public RegisterLoginLogoutController(AdministrateurRepository administrateurRepository, CompagnieRepository compagnieRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RegisterMapper registerMapper, JwtUtils jwtUtils, SmsService smsService) {
        this.administrateurRepository = administrateurRepository;
        this.compagnieRepository = compagnieRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.registerMapper = registerMapper;
        this.jwtUtils = jwtUtils;
        this.smsService = smsService;
    }

    // ==================== Admin ====================
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody AdministrateurRegisterRequest request) {
        if (administrateurRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Cet Admin existe déjà");
        }
        Administrateur admin = registerMapper.toEntity(request);
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        administrateurRepository.save(admin);
        return ResponseEntity.ok("Admin enregistré avec succès");
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) {
        logger.info("Tentative connexion ADMIN: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            Administrateur admin = administrateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Admin non trouvé"));

            String token = jwtUtils.generateTokenAdmin(admin);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "token", token,
                            "adminId", admin.getId()
                    ));

        } catch (Exception ex) {
            logger.error("Échec connexion admin: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erreur", "Mail ou mot de passe invalide"));
        }
    }

    // ==================== Compagnie ====================
    @PostMapping("/register/compagnie")
    public ResponseEntity<?> registerCompagnie(@RequestBody CompagnieRegisterRequest request) {
        if (compagnieRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Cette compagnie existe déjà");
        }
        Compagnie compagnie = registerMapper.toEntity(request);
        compagnie.setRole(Role.COMPAGNIE);
        compagnie.setPassword(passwordEncoder.encode(compagnie.getPassword()));
        compagnieRepository.save(compagnie);
        return ResponseEntity.ok(Map.of("message","Compagnie enregistrée avec succès"));
        //return ResponseEntity.ok("Compagnie enregistrée avec succès");
    }




    @PostMapping("/login/compagnie")
    public ResponseEntity<?> loginCompagnie(@RequestBody LoginRequest request) {
        logger.info("Tentative connexion COMPAGNIE: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            // Récupérer la compagnie depuis la base après authentification
            Compagnie compagnie = compagnieRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Compagnie non trouvée"));

            // Génération du token avec l'ID et le rôle
            String token = jwtUtils.generateTokenCompagnie(compagnie);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "token", token,
                            "compagnieId", compagnie.getId() // Optionnel : retourner l'ID dans la réponse
                    ));

        } catch (Exception ex) {
            logger.error("Échec connexion compagnie: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erreur", "Mail ou mot de passe invalide"));
        }
    }

    // ==================== Client ====================
    @PostMapping("/verify/client")
    public ResponseEntity<?> verifyClient(@RequestParam String phoneNumber, @RequestParam String code) {
        phoneNumber = normalizePhoneNumber(phoneNumber);

        // MODIF: Vérification de l'expiration du code
        OtpData otpData = verificationCodes.get(phoneNumber);
        if (otpData == null) {
            return ResponseEntity.badRequest().body("Aucun code trouvé pour ce numéro.");
        }

        if (System.currentTimeMillis() > otpData.expirationTime) {
            verificationCodes.remove(phoneNumber);
            return ResponseEntity.badRequest().body("Code expiré. Veuillez redémarrer le processus.");
        }

        if (!otpData.code.equals(code)) {
            return ResponseEntity.badRequest().body("Code de vérification incorrect.");
        }

        Client client = clientRepository.findByTelephoneClient(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        client.setVerified(true);
        clientRepository.save(client);

        verificationCodes.remove(phoneNumber);
        return ResponseEntity.ok("Numéro vérifié avec succès !");
    }

    private String normalizePhoneNumber(String phoneNumber) {
        // AJOUT: Nouvelle logique avec libphonenumber
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            return phoneUtil.format(
                    phoneUtil.parse(phoneNumber, "TG"), // TG = Togo par défaut
                    PhoneNumberUtil.PhoneNumberFormat.E164
            );
        } catch (NumberParseException e) {
            // Fallback à l'ancienne logique en cas d'erreur
            phoneNumber = phoneNumber.replaceAll("[^+0-9]", "");

            if (phoneNumber.startsWith("00228")) {
                return "+" + phoneNumber.substring(2);
            } else if (!phoneNumber.startsWith("+")) {
                return "+228" + phoneNumber;
            }
            return phoneNumber;
        }
    }

    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody ClientRegisterRequest request) {
        logger.info(" Requête d'inscription client reçue : {}", request);

        String normalizedPhone = normalizePhoneNumber(request.getTelephoneClient());
        logger.info(" Numéro de téléphone normalisé : {}", normalizedPhone);

        // AJOUT: Validation du format du numéro
        if (!isValidPhoneNumber(normalizedPhone)) {
            logger.warn("Format de numéro invalide : {}", normalizedPhone);
            return ResponseEntity.badRequest().body("Format de numéro invalide");
        }

        if (clientRepository.findByTelephoneClient(normalizedPhone).isPresent()) {
            logger.warn(" Inscription refusée : Le numéro {} est déjà utilisé.", normalizedPhone);
            return ResponseEntity.badRequest().body("Ce numéro est déjà utilisé");
        }

        String verificationCode = VerificationCodeGenerator.generateCode();
        // MODIF: Ajout de l'expiration (5 minutes)
        long expirationTime = System.currentTimeMillis() + 300_000;
        verificationCodes.put(normalizedPhone, new OtpData(verificationCode, expirationTime));
        logger.info(" Code OTP généré pour {} : {}", normalizedPhone, verificationCode);

        try {
            smsService.sendVerificationCode(normalizedPhone, verificationCode);
            logger.info(" Code OTP envoyé avec succès à {}", normalizedPhone);
        } catch (Exception e) {
            logger.error(" Erreur lors de l'envoi du code OTP à {} : {}", normalizedPhone, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'envoi du code OTP.");
        }

        Client client = new Client();
        client.setTelephoneClient(normalizedPhone);
        client.setNomClient(request.getNomClient());
        client.setPrenomClient(request.getPrenomClient());
        client.setRole(Role.CLIENT);
        client.setVerified(false);

        clientRepository.save(client);
        logger.info(" Client enregistré avec succès : {}", client);

        return ResponseEntity.ok("Code de vérification envoyé. Veuillez entrer le code pour confirmer votre numéro.");
    }

    // ==================== NOUVELLES METHODES ====================


    // endpoint pour renvoyer le code otp
    @PostMapping("/resend-otp/client")
    public ResponseEntity<?> resendOtp(@RequestParam String phoneNumber) {
        phoneNumber = normalizePhoneNumber(phoneNumber);

        // Vérification du nombre de tentatives
        ResendAttempt attempt = resendAttempts.getOrDefault(phoneNumber, new ResendAttempt());
        if (attempt.count >= 3) {
            Duration timeSinceFirst = Duration.between(attempt.firstAttempt, Instant.now());
            if (timeSinceFirst.toMinutes() < 10) { // Cooldown de 10 minutes
                long remainingMinutes = 10 - timeSinceFirst.toMinutes();
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Trop de tentatives. Réessayez dans " + remainingMinutes + " minutes");
            } else {
                resendAttempts.remove(phoneNumber);
            }
        }

        Optional<Client> clientOpt = clientRepository.findByTelephoneClient(phoneNumber);
        if (clientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Numéro non enregistré");
        }

        if (clientOpt.get().isVerified()) {
            return ResponseEntity.badRequest().body("Numéro déjà vérifié");
        }

        String newCode = VerificationCodeGenerator.generateCode();
        long expirationTime = System.currentTimeMillis() + 300_000; // 5 minutes

        verificationCodes.put(phoneNumber, new OtpData(newCode, expirationTime));

        // Mise à jour des tentatives
        resendAttempts.put(phoneNumber,
                new ResendAttempt(attempt.count + 1, attempt.firstAttempt));

        try {
            smsService.sendVerificationCode(phoneNumber, newCode);
            return ResponseEntity.ok("Nouveau code OTP envoyé");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'envoi du code");
        }
    }


    private boolean isValidPhoneNumber(String phoneNumber) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            return phoneUtil.isValidNumber(phoneUtil.parse(phoneNumber, null));
        } catch (NumberParseException e) {
            return false;
        }
    }

    // ==================== CLASSES INTERNES ====================
    private static class OtpData {
        String code;
        long expirationTime;

        OtpData(String code, long expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
    }

    private static class ResendAttempt {
        int count;
        final Instant firstAttempt;

        ResendAttempt() {
            this(1, Instant.now());
        }

        ResendAttempt(int count, Instant firstAttempt) {
            this.count = count;
            this.firstAttempt = firstAttempt;
        }
    }




//    // ==================== Logout ====================
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
//        // Vérifie si le token est valide avant de le supprimer (si tu gères une liste noire)
//        if (token != null && token.startsWith("Bearer ")) {
//            String jwt = token.substring(7); // Supprime "Bearer "
//            jwtUtils.invalidateToken(jwt); // Fonction à implémenter si tu veux gérer une liste noire
//            return ResponseEntity.ok("Déconnexion réussie");
//        }
//        return ResponseEntity.badRequest().body("Token invalide");
//    }
}