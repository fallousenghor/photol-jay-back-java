package com.photoljay.photoljay.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.photoljay.photoljay.dto.request.CategorieRequest;
import com.photoljay.photoljay.dto.response.CategorieResponse;
import com.photoljay.photoljay.entity.Categorie;
import com.photoljay.photoljay.exception.BadRequestException;
import com.photoljay.photoljay.exception.ResourceNotFoundException;
import com.photoljay.photoljay.repository.CategorieRepository;
import com.photoljay.photoljay.util.DtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorieService {
    
    @Autowired
    private CategorieRepository categorieRepository;
    
    /**
     * Créer une nouvelle catégorie
     */
    @Transactional
    public CategorieResponse creerCategorie(CategorieRequest request) {
        // Vérifier si le nom existe déjà
        if (categorieRepository.existsByNom(request.getNom())) {
            throw new BadRequestException("Une catégorie avec ce nom existe déjà");
        }
        
        Categorie categorie = new Categorie();
        categorie.setNom(request.getNom());
        categorie.setDescription(request.getDescription());
        categorie.setIcone(request.getIcone());
        categorie.setActive(true);
        categorie.setDateCreation(LocalDateTime.now());
        
        Categorie saved = categorieRepository.save(categorie);
        
        return DtoMapper.toCategorieResponse(saved);
    }
    
    /**
     * Récupérer toutes les catégories actives
     */
    public List<CategorieResponse> getCategoriesActives() {
        List<Categorie> categories = categorieRepository.findByActiveTrue();
        
        return categories.stream()
            .map(DtoMapper::toCategorieResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupérer toutes les catégories (admin)
     */
    public List<CategorieResponse> getToutesCategories() {
        List<Categorie> categories = categorieRepository.findAll();
        
        return categories.stream()
            .map(DtoMapper::toCategorieResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupérer une catégorie par ID
     */
    public CategorieResponse getCategorieById(Long id) {
        Categorie categorie = categorieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        
        return DtoMapper.toCategorieResponse(categorie);
    }
    
    /**
     * Mettre à jour une catégorie
     */
    @Transactional
    public CategorieResponse updateCategorie(Long id, CategorieRequest request) {
        Categorie categorie = categorieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        
        // Vérifier si le nouveau nom existe déjà (sauf pour cette catégorie)
        if (!categorie.getNom().equals(request.getNom()) && categorieRepository.existsByNom(request.getNom())) {
            throw new BadRequestException("Une catégorie avec ce nom existe déjà");
        }
        
        categorie.setNom(request.getNom());
        categorie.setDescription(request.getDescription());
        categorie.setIcone(request.getIcone());
        
        Categorie updated = categorieRepository.save(categorie);
        
        return DtoMapper.toCategorieResponse(updated);
    }
    
    /**
     * Désactiver une catégorie
     */
    @Transactional
    public void desactiverCategorie(Long id) {
        Categorie categorie = categorieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        
        categorie.setActive(false);
        categorieRepository.save(categorie);
    }
    
    /**
     * Activer une catégorie
     */
    @Transactional
    public void activerCategorie(Long id) {
        Categorie categorie = categorieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        
        categorie.setActive(true);
        categorieRepository.save(categorie);
    }
}