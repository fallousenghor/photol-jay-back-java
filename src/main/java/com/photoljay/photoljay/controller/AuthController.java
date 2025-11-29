package com.photoljay.photoljay.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.photoljay.photoljay.dto.request.LoginRequest;
import com.photoljay.photoljay.dto.request.RegisterRequest;
import com.photoljay.photoljay.dto.response.ApiResponse;
import com.photoljay.photoljay.dto.response.AuthResponse;
import com.photoljay.photoljay.dto.response.UtilisateurResponse;
import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.exception.BadRequestException;
import com.photoljay.photoljay.security.SecurityUtils;
import com.photoljay.photoljay.service.AuthService;
import com.photoljay.photoljay.service.UtilisateurService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * POST /api/auth/register
     * Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        UtilisateurResponse utilisateur = utilisateurService.creerUtilisateur(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Inscription réussie", utilisateur));
    }
    
    /**
     * POST /api/auth/login
     * Connexion d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        // Récupérer l'utilisateur pour vérifier qu'il est actif
        Utilisateur utilisateur = utilisateurService.findByEmail(request.getEmail());
        
        if (!utilisateur.getActif()) {
            throw new BadRequestException("Votre compte a été désactivé");
        }
        
        // Authentifier et générer le token JWT
        String token = authService.authenticateUser(request.getEmail(), request.getMotDePasse());
        
        AuthResponse authResponse = new AuthResponse(
            token,
            utilisateur.getId(),
            utilisateur.getEmail(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getTelephone(),
            utilisateur.getRole().name()
        );
        
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie", authResponse));
    }
    
    /**
     * GET /api/auth/me
     * Récupérer les informations de l'utilisateur connecté
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (userId == null) {
            throw new BadRequestException("Utilisateur non authentifié");
        }
        
        UtilisateurResponse utilisateur = utilisateurService.getUtilisateurById(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Utilisateur récupéré", utilisateur));
    }
}