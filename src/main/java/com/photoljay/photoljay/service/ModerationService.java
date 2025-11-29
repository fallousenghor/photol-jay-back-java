package com.photoljay.photoljay.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.photoljay.photoljay.dto.request.ModerationRequest;
import com.photoljay.photoljay.dto.response.ProduitResponse;
import com.photoljay.photoljay.entity.HistoriqueModeration;
import com.photoljay.photoljay.entity.Produit;
import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.enums.ActionModeration;
import com.photoljay.photoljay.enums.Role;
import com.photoljay.photoljay.enums.StatutProduit;
import com.photoljay.photoljay.exception.BadRequestException;
import com.photoljay.photoljay.exception.ResourceNotFoundException;
import com.photoljay.photoljay.exception.UnauthorizedException;
import com.photoljay.photoljay.repository.HistoriqueModerationRepository;
import com.photoljay.photoljay.repository.ProduitRepository;
import com.photoljay.photoljay.repository.UtilisateurRepository;
import com.photoljay.photoljay.util.DtoMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModerationService {
    
    @Autowired
    private ProduitRepository produitRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private HistoriqueModerationRepository historiqueModerationRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Récupérer tous les produits en attente de modération
     */
    public Page<ProduitResponse> getProduitsEnAttente(Pageable pageable) {
        Page<Produit> produits = produitRepository.findByStatutOrderByDateCreationAsc(
            StatutProduit.EN_ATTENTE, 
            pageable
        );
        
        return produits.map(DtoMapper::toProduitResponse);
    }
    
    /**
     * Approuver un produit
     */
    @Transactional
    public ProduitResponse approuverProduit(Long produitId, Long moderateurId, ModerationRequest request) {
        // 1. Vérifier que l'utilisateur est bien modérateur ou admin
        Utilisateur moderateur = utilisateurRepository.findById(moderateurId)
            .orElseThrow(() -> new ResourceNotFoundException("Modérateur non trouvé"));
        
        if (moderateur.getRole() != Role.MODERATEUR && moderateur.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent approuver des produits");
        }
        
        // 2. Récupérer le produit
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        // 3. Vérifier que le produit est en attente
        if (produit.getStatut() != StatutProduit.EN_ATTENTE) {
            throw new BadRequestException("Ce produit n'est pas en attente de modération");
        }
        
        // 4. Enregistrer l'historique
        StatutProduit ancienStatut = produit.getStatut();
        
        // 5. Modifier le statut du produit
        produit.setStatut(StatutProduit.APPROUVE);
        produit.setModerateur(moderateur);
        produit.setDateModerationDecision(LocalDateTime.now());
        produit.setDateModification(LocalDateTime.now());
        
        Produit produitApprouve = produitRepository.save(produit);
        
        // 6. Créer l'entrée dans l'historique de modération
        creerHistoriqueModeration(
            produit, 
            moderateur, 
            ActionModeration.APPROUVE, 
            request.getCommentaire(),
            ancienStatut,
            StatutProduit.APPROUVE
        );
        
        // 7. Notifier le vendeur
        notificationService.notifierProduitApprouve(produit);
        
        return DtoMapper.toProduitResponse(produitApprouve);
    }
    
    /**
     * Rejeter un produit
     */
    @Transactional
    public ProduitResponse rejeterProduit(Long produitId, Long moderateurId, ModerationRequest request) {
        // 1. Vérifier que l'utilisateur est bien modérateur ou admin
        Utilisateur moderateur = utilisateurRepository.findById(moderateurId)
            .orElseThrow(() -> new ResourceNotFoundException("Modérateur non trouvé"));
        
        if (moderateur.getRole() != Role.MODERATEUR && moderateur.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent rejeter des produits");
        }
        
        // 2. Récupérer le produit
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        // 3. Vérifier que le produit est en attente
        if (produit.getStatut() != StatutProduit.EN_ATTENTE) {
            throw new BadRequestException("Ce produit n'est pas en attente de modération");
        }
        
        // 4. Vérifier qu'un motif de rejet est fourni
        if (request.getCommentaire() == null || request.getCommentaire().trim().isEmpty()) {
            throw new BadRequestException("Un motif de rejet est obligatoire");
        }
        
        // 5. Enregistrer l'historique
        StatutProduit ancienStatut = produit.getStatut();
        
        // 6. Modifier le statut du produit
        produit.setStatut(StatutProduit.REJETE);
        produit.setModerateur(moderateur);
        produit.setMotifRejet(request.getCommentaire());
        produit.setDateModerationDecision(LocalDateTime.now());
        produit.setDateModification(LocalDateTime.now());
        
        Produit produitRejete = produitRepository.save(produit);
        
        // 7. Créer l'entrée dans l'historique de modération
        creerHistoriqueModeration(
            produit, 
            moderateur, 
            ActionModeration.REJETE, 
            request.getCommentaire(),
            ancienStatut,
            StatutProduit.REJETE
        );
        
        // 8. Notifier le vendeur
        notificationService.notifierProduitRejete(produit, request.getCommentaire());
        
        return DtoMapper.toProduitResponse(produitRejete);
    }
    
    /**
     * Suspendre un produit (produit déjà approuvé)
     */
    @Transactional
    public ProduitResponse suspendreProduit(Long produitId, Long moderateurId, ModerationRequest request) {
        Utilisateur moderateur = utilisateurRepository.findById(moderateurId)
            .orElseThrow(() -> new ResourceNotFoundException("Modérateur non trouvé"));
        
        if (moderateur.getRole() != Role.MODERATEUR && moderateur.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent suspendre des produits");
        }
        
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        StatutProduit ancienStatut = produit.getStatut();
        
        produit.setStatut(StatutProduit.SUSPENDU);
        produit.setModerateur(moderateur);
        produit.setMotifRejet(request.getCommentaire());
        produit.setDateModerationDecision(LocalDateTime.now());
        produit.setDateModification(LocalDateTime.now());
        
        Produit produitSuspendu = produitRepository.save(produit);
        
        creerHistoriqueModeration(
            produit, 
            moderateur, 
            ActionModeration.SUSPENDU, 
            request.getCommentaire(),
            ancienStatut,
            StatutProduit.SUSPENDU
        );
        
        return DtoMapper.toProduitResponse(produitSuspendu);
    }
    
    /**
     * Récupérer l'historique de modération d'un produit
     */
    public List<HistoriqueModeration> getHistoriqueProduit(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        return historiqueModerationRepository.findByProduitOrderByDateActionDesc(produit);
    }
    
    /**
     * Compter les produits en attente
     */
    public long compterProduitsEnAttente() {
        return produitRepository.countByStatut(StatutProduit.EN_ATTENTE);
    }
    
    /**
     * Statistiques de modération
     */
    public Map<String, Object> getStatistiquesModeration() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("en_attente", produitRepository.countByStatut(StatutProduit.EN_ATTENTE));
        stats.put("approuves", produitRepository.countByStatut(StatutProduit.APPROUVE));
        stats.put("rejetes", produitRepository.countByStatut(StatutProduit.REJETE));
        stats.put("suspendus", produitRepository.countByStatut(StatutProduit.SUSPENDU));
        
        return stats;
    }
    
    /**
     * Récupérer les actions récentes d'un modérateur
     */
    public List<HistoriqueModeration> getActionsModerateur(Long moderateurId) {
        Utilisateur moderateur = utilisateurRepository.findById(moderateurId)
            .orElseThrow(() -> new ResourceNotFoundException("Modérateur non trouvé"));
        
        return historiqueModerationRepository.findByModerateurOrderByDateActionDesc(moderateur);
    }
    
    /**
     * Récupérer les 20 dernières actions de modération (pour dashboard admin)
     */
    public List<HistoriqueModeration> getDernieresActions() {
        return historiqueModerationRepository.findTop20ByOrderByDateActionDesc();
    }
    
    /**
     * Méthode privée pour créer une entrée dans l'historique
     */
    private void creerHistoriqueModeration(Produit produit, Utilisateur moderateur, 
                                          ActionModeration action, String commentaire,
                                          StatutProduit statutAvant, StatutProduit statutApres) {
        HistoriqueModeration historique = new HistoriqueModeration();
        historique.setProduit(produit);
        historique.setModerateur(moderateur);
        historique.setAction(action);
        historique.setCommentaire(commentaire);
        historique.setStatutAvant(statutAvant);
        historique.setStatutApres(statutApres);
        historique.setDateAction(LocalDateTime.now());
        
        historiqueModerationRepository.save(historique);
    }
}