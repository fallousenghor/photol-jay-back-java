package com.photoljay.photoljay.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.photoljay.photoljay.dto.request.ModerationRequest;
import com.photoljay.photoljay.dto.response.ApiResponse;
import com.photoljay.photoljay.dto.response.ProduitResponse;
import com.photoljay.photoljay.entity.HistoriqueModeration;
import com.photoljay.photoljay.security.SecurityUtils;
import com.photoljay.photoljay.service.ModerationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moderation")
@CrossOrigin(origins = "*")
public class ModerationController {

    @Autowired
    private ModerationService moderationService;

    /**
     * GET /api/moderation/en-attente
     * Récupérer tous les produits en attente de modération
     */
    @GetMapping("/en-attente")
    public ResponseEntity<ApiResponse> getProduitsEnAttente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dateCreation"));
        Page<ProduitResponse> produits = moderationService.getProduitsEnAttente(pageable);

        return ResponseEntity.ok(ApiResponse.success("Produits en attente récupérés", produits));
    }

    /**
     * POST /api/moderation/{produitId}/approuver
     * Approuver un produit
     */
    @PostMapping("/{produitId}/approuver")
    public ResponseEntity<ApiResponse> approuverProduit(
            @PathVariable Long produitId,
            @Valid @RequestBody(required = false) ModerationRequest request) {

        Long moderateurId = SecurityUtils.getCurrentUserId();

        if (request == null) {
            request = new ModerationRequest();
            request.setAction("APPROUVER");
        }

        ProduitResponse produit = moderationService.approuverProduit(produitId, moderateurId, request);

        return ResponseEntity.ok(ApiResponse.success("Produit approuvé avec succès", produit));
    }

    /**
     * POST /api/moderation/{produitId}/rejeter
     * Rejeter un produit
     */
    @PostMapping("/{produitId}/rejeter")
    public ResponseEntity<ApiResponse> rejeterProduit(
            @PathVariable Long produitId,
            @Valid @RequestBody ModerationRequest request) {

        Long moderateurId = SecurityUtils.getCurrentUserId();

        ProduitResponse produit = moderationService.rejeterProduit(produitId, moderateurId, request);

        return ResponseEntity.ok(ApiResponse.success("Produit rejeté", produit));
    }

    /**
     * POST /api/moderation/{produitId}/suspendre
     * Suspendre un produit
     */
    @PostMapping("/{produitId}/suspendre")
    public ResponseEntity<ApiResponse> suspendreProduit(
            @PathVariable Long produitId,
            @RequestParam Long moderateurId,
            @Valid @RequestBody ModerationRequest request) {

        ProduitResponse produit = moderationService.suspendreProduit(produitId, moderateurId, request);

        return ResponseEntity.ok(ApiResponse.success("Produit suspendu", produit));
    }

    /**
     * GET /api/moderation/produit/{produitId}/historique
     * Récupérer l'historique de modération d'un produit
     */
    @GetMapping("/produit/{produitId}/historique")
    public ResponseEntity<ApiResponse> getHistoriqueProduit(@PathVariable Long produitId) {
        List<HistoriqueModeration> historique = moderationService.getHistoriqueProduit(produitId);

        return ResponseEntity.ok(ApiResponse.success("Historique récupéré", historique));
    }

    /**
     * GET /api/moderation/stats
     * Statistiques de modération
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getStatistiques() {
        Map<String, Object> stats = moderationService.getStatistiquesModeration();

        return ResponseEntity.ok(ApiResponse.success("Statistiques récupérées", stats));
    }

    /**
     * GET /api/moderation/moderateur/{moderateurId}/actions
     * Récupérer les actions d'un modérateur
     */
    @GetMapping("/moderateur/{moderateurId}/actions")
    public ResponseEntity<ApiResponse> getActionsModerateur(@PathVariable Long moderateurId) {
        List<HistoriqueModeration> actions = moderationService.getActionsModerateur(moderateurId);

        return ResponseEntity.ok(ApiResponse.success("Actions du modérateur récupérées", actions));
    }

    /**
     * GET /api/moderation/dernieres-actions
     * Récupérer les 20 dernières actions de modération
     */
    @GetMapping("/dernieres-actions")
    public ResponseEntity<ApiResponse> getDernieresActions() {
        List<HistoriqueModeration> actions = moderationService.getDernieresActions();

        return ResponseEntity.ok(ApiResponse.success("Dernières actions récupérées", actions));
    }

    /**
     * GET /api/moderation/count-en-attente
     * Compter les produits en attente
     */
    @GetMapping("/count-en-attente")
    public ResponseEntity<ApiResponse> compterEnAttente() {
        long count = moderationService.compterProduitsEnAttente();

        return ResponseEntity.ok(ApiResponse.success("Nombre de produits en attente", count));
    }
}