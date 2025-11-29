package com.photoljay.photoljay.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.photoljay.photoljay.dto.request.ProduitRequest;
import com.photoljay.photoljay.dto.response.ApiResponse;
import com.photoljay.photoljay.dto.response.ProduitResponse;
import com.photoljay.photoljay.security.SecurityUtils;
import com.photoljay.photoljay.service.ProduitService;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "*")
public class ProduitController {
    
    @Autowired
    private ProduitService produitService;
    
   /**
 * POST /api/produits
 * Créer un nouveau produit (avec photo)
 */
@PostMapping(consumes = {"multipart/form-data"})
public ResponseEntity<ApiResponse> creerProduit(
        @RequestPart("produit") @Valid ProduitRequest request,
        @RequestPart("photo") MultipartFile photo) {
    
    // Récupérer l'ID du vendeur depuis le token JWT
    Long vendeurId = SecurityUtils.getCurrentUserId();
    
    ProduitResponse produit = produitService.creerProduit(request, photo, vendeurId);
    
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success("Produit créé avec succès. En attente de validation.", produit));
}
    
    /**
     * GET /api/produits
     * Récupérer tous les produits approuvés (pagination)
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getProduitsApprouves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ProduitResponse> produits = produitService.getProduitsApprouves(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Produits récupérés", produits));
    }
    
    /**
     * GET /api/produits/{id}
     * Récupérer un produit par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduitById(@PathVariable Long id) {
        ProduitResponse produit = produitService.getProduitById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Produit récupéré", produit));
    }
    
    /**
     * GET /api/produits/categorie/{categorieId}
     * Récupérer les produits d'une catégorie
     */
    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<ApiResponse> getProduitsParCategorie(
            @PathVariable Long categorieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        Page<ProduitResponse> produits = produitService.getProduitsParCategorie(categorieId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Produits récupérés", produits));
    }
    
    /**
     * GET /api/produits/recherche
     * Rechercher des produits par nom
     */
    @GetMapping("/recherche")
    public ResponseEntity<ApiResponse> rechercherProduits(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        Page<ProduitResponse> produits = produitService.rechercherProduits(q, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Produits trouvés", produits));
    }
    
    /**
     * GET /api/produits/recherche-avancee
     * Recherche avancée avec filtres multiples
     */
    @GetMapping("/recherche-avancee")
    public ResponseEntity<ApiResponse> rechercheAvancee(
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        Page<ProduitResponse> produits = produitService.rechercheAvancee(categorieId, q, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Produits trouvés", produits));
    }
    
    /**
     * GET /api/produits/vendeur/{vendeurId}
     * Récupérer tous les produits d'un vendeur
     */
    @GetMapping("/vendeur/{vendeurId}")
    public ResponseEntity<ApiResponse> getProduitsVendeur(@PathVariable Long vendeurId) {
        List<ProduitResponse> produits = produitService.getProduitsVendeur(vendeurId);
        
        return ResponseEntity.ok(ApiResponse.success("Produits du vendeur récupérés", produits));
    }
    
    /**
     * GET /api/produits/vendeur/{vendeurId}/statut/{statut}
     * Récupérer les produits d'un vendeur par statut
     */
    @GetMapping("/vendeur/{vendeurId}/statut/{statut}")
    public ResponseEntity<ApiResponse> getProduitsVendeurParStatut(
            @PathVariable Long vendeurId,
            @PathVariable String statut) {
        
        List<ProduitResponse> produits = produitService.getProduitsVendeurParStatut(
            vendeurId, 
            com.photoljay.photoljay.enums.StatutProduit.valueOf(statut.toUpperCase())
        );
        
        return ResponseEntity.ok(ApiResponse.success("Produits récupérés", produits));
    }
    
    /**
 * PUT /api/produits/{id}/marquer-vendu
 * Marquer un produit comme vendu
 */
@PutMapping("/{id}/marquer-vendu")
public ResponseEntity<ApiResponse> marquerCommeVendu(@PathVariable Long id) {
    Long vendeurId = SecurityUtils.getCurrentUserId();
    
    ProduitResponse produit = produitService.marquerCommeVendu(id, vendeurId);
    
    return ResponseEntity.ok(ApiResponse.success("Produit marqué comme vendu", produit));
}
    
    /**
     * PUT /api/produits/{id}/archiver
     * Archiver un produit
     */
    @PutMapping("/{id}/archiver")
    public ResponseEntity<ApiResponse> archiverProduit(
            @PathVariable Long id,
            @RequestParam Long vendeurId) {
        
        produitService.archiverProduit(id, vendeurId);
        
        return ResponseEntity.ok(ApiResponse.success("Produit archivé"));
    }
    
   /**
 * DELETE /api/produits/{id}
 * Supprimer un produit
 */
@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse> supprimerProduit(@PathVariable Long id) {
    Long vendeurId = SecurityUtils.getCurrentUserId();
    
    produitService.supprimerProduit(id, vendeurId);
    
    return ResponseEntity.ok(ApiResponse.success("Produit supprimé"));
}
    
    /**
     * GET /api/produits/plus-vus
     * Récupérer les produits les plus consultés
     */
    @GetMapping("/plus-vus")
    public ResponseEntity<ApiResponse> getProduitsPlusVus() {
        List<ProduitResponse> produits = produitService.getProduitsPlusVus();
        
        return ResponseEntity.ok(ApiResponse.success("Produits les plus vus", produits));
    }
}