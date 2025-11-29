package com.photoljay.photoljay.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.photoljay.photoljay.dto.request.RegisterRequest;
import com.photoljay.photoljay.dto.response.UtilisateurResponse;
import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.enums.Role;
import com.photoljay.photoljay.exception.BadRequestException;
import com.photoljay.photoljay.exception.ResourceNotFoundException;
import com.photoljay.photoljay.repository.UtilisateurRepository;
import com.photoljay.photoljay.util.DtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Créer un nouvel utilisateur (inscription)
     */
    @Transactional
    public UtilisateurResponse creerUtilisateur(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }
        
        // Vérifier si le téléphone existe déjà
        if (utilisateurRepository.existsByTelephone(request.getTelephone())) {
            throw new BadRequestException("Ce numéro de téléphone est déjà utilisé");
        }
        
        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        
        // Définir le rôle
        try {
            utilisateur.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rôle invalide. Utilisez VENDEUR ou ACHETEUR");
        }
        
        utilisateur.setAdresse(request.getAdresse());
        utilisateur.setVille(request.getVille());
        utilisateur.setPays(request.getPays());
        utilisateur.setActif(true);
        utilisateur.setEmailVerifie(false);
        utilisateur.setTelephoneVerifie(false);
        utilisateur.setDateInscription(LocalDateTime.now());
        
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        
        return DtoMapper.toUtilisateurResponse(savedUtilisateur);
    }
    
    /**
     * Récupérer un utilisateur par ID
     */
    public UtilisateurResponse getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        return DtoMapper.toUtilisateurResponse(utilisateur);
    }
    
    /**
     * Récupérer un utilisateur par email
     */
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }
    
    /**
     * Récupérer tous les modérateurs actifs
     */
    public List<UtilisateurResponse> getModeratorsActifs() {
        List<Utilisateur> moderateurs = utilisateurRepository.findByRoleAndActifTrue(Role.MODERATEUR);
        
        return moderateurs.stream()
            .map(DtoMapper::toUtilisateurResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Désactiver un utilisateur
     */
    @Transactional
    public void desactiverUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
    }
    
    /**
     * Mettre à jour le profil
     */
    @Transactional
    public UtilisateurResponse updateProfil(Long id, RegisterRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setAdresse(request.getAdresse());
        utilisateur.setVille(request.getVille());
        utilisateur.setPays(request.getPays());
        utilisateur.setDateModification(LocalDateTime.now());
        
        Utilisateur updated = utilisateurRepository.save(utilisateur);
        
        return DtoMapper.toUtilisateurResponse(updated);
    }
}