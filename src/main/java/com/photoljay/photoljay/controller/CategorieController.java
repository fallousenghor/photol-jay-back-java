package com.photoljay.photoljay.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.photoljay.photoljay.dto.request.CategorieRequest;
import com.photoljay.photoljay.dto.response.ApiResponse;
import com.photoljay.photoljay.dto.response.CategorieResponse;
import com.photoljay.photoljay.service.CategorieService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategorieController {
    
    @Autowired
    private CategorieService categorieService;
    
    /**
     * POST /api/categories
     * Créer une nouvelle catégorie (Admin)
     */
    @PostMapping
    public ResponseEntity<ApiResponse> creerCategorie(@Valid @RequestBody CategorieRequest request) {
        CategorieResponse categorie = categorieService.creerCategorie(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Catégorie créée", categorie));
    }
    
    /**
     * GET /api/categories
     * Récupérer toutes les catégories actives
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getCategoriesActives() {
        List<CategorieResponse> categories = categorieService.getCategoriesActives();
        
        return ResponseEntity.ok(ApiResponse.success("Catégories récupérées", categories));
    }
    
    /**
     * GET /api/categories/toutes
     * Récupérer toutes les catégories (Admin)
     */
    @GetMapping("/toutes")
    public ResponseEntity<ApiResponse> getToutesCategories() {
        List<CategorieResponse> categories = categorieService.getToutesCategories();
        
        return ResponseEntity.ok(ApiResponse.success("Toutes les catégories récupérées", categories));
    }
    
    /**
     * GET /api/categories/{id}
     * Récupérer une catégorie par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategorieById(@PathVariable Long id) {
        CategorieResponse categorie = categorieService.getCategorieById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Catégorie récupérée", categorie));
    }
    
    /**
     * PUT /api/categories/{id}
     * Mettre à jour une catégorie (Admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategorie(
            @PathVariable Long id,
            @Valid @RequestBody CategorieRequest request) {
        
        CategorieResponse categorie = categorieService.updateCategorie(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Catégorie mise à jour", categorie));
    }
    
    /**
     * PUT /api/categories/{id}/desactiver
     * Désactiver une catégorie (Admin)
     */
    @PutMapping("/{id}/desactiver")
    public ResponseEntity<ApiResponse> desactiverCategorie(@PathVariable Long id) {
        categorieService.desactiverCategorie(id);
        
        return ResponseEntity.ok(ApiResponse.success("Catégorie désactivée"));
    }
    
    /**
     * PUT /api/categories/{id}/activer
     * Activer une catégorie (Admin)
     */
    @PutMapping("/{id}/activer")
    public ResponseEntity<ApiResponse> activerCategorie(@PathVariable Long id) {
        categorieService.activerCategorie(id);
        
        return ResponseEntity.ok(ApiResponse.success("Catégorie activée"));
    }
}