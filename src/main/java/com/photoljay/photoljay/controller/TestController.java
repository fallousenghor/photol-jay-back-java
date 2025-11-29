package com.photoljay.photoljay.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.photoljay.photoljay.entity.Categorie;
import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.enums.Role;
import com.photoljay.photoljay.repository.CategorieRepository;
import com.photoljay.photoljay.repository.UtilisateurRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private CategorieRepository categorieRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @GetMapping("/init-data")
    public ResponseEntity<Map<String, String>> initData() {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Créer des catégories de test
            if (categorieRepository.count() == 0) {
                Categorie electronique = new Categorie();
                electronique.setNom("Électronique");
                electronique.setDescription("Smartphones, ordinateurs, accessoires");
                electronique.setActive(true);
                electronique.setDateCreation(LocalDateTime.now());
                categorieRepository.save(electronique);
                
                Categorie vetements = new Categorie();
                vetements.setNom("Vêtements");
                vetements.setDescription("Mode homme, femme, enfant");
                vetements.setActive(true);
                vetements.setDateCreation(LocalDateTime.now());
                categorieRepository.save(vetements);
                
                Categorie maison = new Categorie();
                maison.setNom("Maison & Jardin");
                maison.setDescription("Meubles, décoration, jardinage");
                maison.setActive(true);
                maison.setDateCreation(LocalDateTime.now());
                categorieRepository.save(maison);
                
                response.put("categories", "3 catégories créées");
            } else {
                response.put("categories", "Catégories déjà existantes");
            }
            
            // Créer un admin de test
            if (!utilisateurRepository.existsByEmail("admin@marketplace.com")) {
                Utilisateur admin = new Utilisateur();
                admin.setEmail("admin@marketplace.com");
                admin.setTelephone("+221771234567");
                admin.setMotDePasse(passwordEncoder.encode("admin123"));
                admin.setNom("Administrateur");
                admin.setPrenom("Système");
                admin.setRole(Role.ADMIN);
                admin.setActif(true);
                admin.setEmailVerifie(true);
                admin.setTelephoneVerifie(true);
                admin.setDateInscription(LocalDateTime.now());
                utilisateurRepository.save(admin);
                
                response.put("admin", "Admin créé: admin@marketplace.com / admin123");
            } else {
                response.put("admin", "Admin déjà existant");
            }
            
            // Créer un modérateur de test
            if (!utilisateurRepository.existsByEmail("moderateur@marketplace.com")) {
                Utilisateur moderateur = new Utilisateur();
                moderateur.setEmail("moderateur@marketplace.com");
                moderateur.setTelephone("+221772234567");
                moderateur.setMotDePasse(passwordEncoder.encode("modo123"));
                moderateur.setNom("Modérateur");
                moderateur.setPrenom("Test");
                moderateur.setRole(Role.MODERATEUR);
                moderateur.setActif(true);
                moderateur.setEmailVerifie(true);
                moderateur.setTelephoneVerifie(true);
                moderateur.setDateInscription(LocalDateTime.now());
                utilisateurRepository.save(moderateur);
                
                response.put("moderateur", "Modérateur créé: moderateur@marketplace.com / modo123");
            } else {
                response.put("moderateur", "Modérateur déjà existant");
            }
            
            // Créer un vendeur de test
            if (!utilisateurRepository.existsByEmail("vendeur@marketplace.com")) {
                Utilisateur vendeur = new Utilisateur();
                vendeur.setEmail("vendeur@marketplace.com");
                vendeur.setTelephone("+221773234567");
                vendeur.setMotDePasse(passwordEncoder.encode("vendeur123"));
                vendeur.setNom("Diop");
                vendeur.setPrenom("Mamadou");
                vendeur.setRole(Role.VENDEUR);
                vendeur.setActif(true);
                vendeur.setEmailVerifie(true);
                vendeur.setTelephoneVerifie(true);
                vendeur.setVille("Dakar");
                vendeur.setPays("Sénégal");
                vendeur.setDateInscription(LocalDateTime.now());
                utilisateurRepository.save(vendeur);
                
                response.put("vendeur", "Vendeur créé: vendeur@marketplace.com / vendeur123");
            } else {
                response.put("vendeur", "Vendeur déjà existant");
            }
            
            response.put("status", "SUCCESS");
            response.put("message", "Données de test initialisées avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total_utilisateurs", utilisateurRepository.count());
        stats.put("total_categories", categorieRepository.count());
        stats.put("utilisateurs_actifs", utilisateurRepository.findByActifTrue().size());
        
        return ResponseEntity.ok(stats);
    }
}