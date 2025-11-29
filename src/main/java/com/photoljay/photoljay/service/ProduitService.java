package com.photoljay.photoljay.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.photoljay.photoljay.dto.request.ProduitRequest;
import com.photoljay.photoljay.dto.response.ProduitResponse;
import com.photoljay.photoljay.entity.Categorie;
import com.photoljay.photoljay.entity.MetadataPhoto;
import com.photoljay.photoljay.entity.Produit;
import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.enums.Role;
import com.photoljay.photoljay.enums.StatutProduit;
import com.photoljay.photoljay.exception.ResourceNotFoundException;
import com.photoljay.photoljay.exception.UnauthorizedException;
import com.photoljay.photoljay.repository.CategorieRepository;
import com.photoljay.photoljay.repository.ProduitRepository;
import com.photoljay.photoljay.repository.UtilisateurRepository;
import com.photoljay.photoljay.util.DtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProduitService {
    
    @Autowired
    private ProduitRepository produitRepository;
    
    @Autowired
    private CategorieRepository categorieRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private StorageService storageService;
    
    @Autowired
    private MetadataExtractionService metadataExtractionService;
    
    @Autowired
    private PhotoValidationService photoValidationService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Créer un nouveau produit (par un vendeur)
     */
    @Transactional
    public ProduitResponse creerProduit(ProduitRequest request, MultipartFile photo, Long vendeurId) {
        // 1. Vérifier que l'utilisateur est bien un vendeur
        Utilisateur vendeur = utilisateurRepository.findById(vendeurId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendeur non trouvé"));
        
        if (vendeur.getRole() != Role.VENDEUR) {
            throw new UnauthorizedException("Seuls les vendeurs peuvent créer des produits");
        }
        
        // 2. Valider le format de la photo
        photoValidationService.validateFileFormat(photo);
        
        // 3. Extraire les métadonnées EXIF
        MetadataPhoto metadataPhoto = metadataExtractionService.extractMetadata(photo);
        
        // 4. Valider l'authenticité de la photo
        photoValidationService.validatePhotoAuthenticity(metadataPhoto);
        
        // 5. Enregistrer la photo sur le disque
        String filename = storageService.store(photo);
        String photoUrl = "uploads/produits/" + filename;
        
        // 6. Vérifier que la catégorie existe
        Categorie categorie = categorieRepository.findById(request.getCategorieId())
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        
        // 7. Créer le produit
        Produit produit = new Produit();
        produit.setNom(request.getNom());
        produit.setDescription(request.getDescription());
        produit.setPrix(request.getPrix());
        produit.setPhotoUrl(photoUrl);
        produit.setStatut(StatutProduit.EN_ATTENTE);
        produit.setVendeur(vendeur);
        produit.setCategorie(categorie);
        produit.setMetadataPhoto(metadataPhoto);
        produit.setDisponible(true);
        produit.setNombreVues(0);
        produit.setDateCreation(LocalDateTime.now());
        
        Produit savedProduit = produitRepository.save(produit);
        
        // 8. Notifier les modérateurs
        List<Utilisateur> moderateurs = utilisateurRepository.findByRoleAndActifTrue(Role.MODERATEUR);
        for (Utilisateur moderateur : moderateurs) {
            notificationService.notifierNouveauProduitModeration(savedProduit, moderateur);
        }
        
        return DtoMapper.toProduitResponse(savedProduit);
    }
    
    /**
     * Récupérer tous les produits approuvés (pour acheteurs) avec pagination
     */
    public Page<ProduitResponse> getProduitsApprouves(Pageable pageable) {
        Page<Produit> produits = produitRepository.findByStatutAndDisponibleTrue(
            StatutProduit.APPROUVE, 
            pageable
        );
        
        return produits.map(DtoMapper::toProduitResponse);
    }
    
    /**
     * Rechercher des produits par catégorie
     */
    public Page<ProduitResponse> getProduitsParCategorie(Long categorieId, Pageable pageable) {
        Page<Produit> produits = produitRepository.findByStatutAndCategorieIdAndDisponibleTrue(
            StatutProduit.APPROUVE,
            categorieId,
            pageable
        );
        
        return produits.map(DtoMapper::toProduitResponse);
    }
    
    /**
     * Rechercher des produits par nom
     */
    public Page<ProduitResponse> rechercherProduits(String recherche, Pageable pageable) {
        Page<Produit> produits = produitRepository.findByStatutAndNomContainingIgnoreCaseAndDisponibleTrue(
            StatutProduit.APPROUVE,
            recherche,
            pageable
        );
        
        return produits.map(DtoMapper::toProduitResponse);
    }
    
    /**
     * Recherche avancée avec filtres multiples
     */
    public Page<ProduitResponse> rechercheAvancee(Long categorieId, String recherche, Pageable pageable) {
        Page<Produit> produits = produitRepository.rechercheAvancee(
            StatutProduit.APPROUVE,
            categorieId,
            recherche,
            pageable
        );
        
        return produits.map(DtoMapper::toProduitResponse);
    }
    
    /**
     * Récupérer un produit par ID (avec incrémentation des vues)
     */
    @Transactional
    public ProduitResponse getProduitById(Long id) {
        Produit produit = produitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        // Incrémenter le nombre de vues
        produit.setNombreVues(produit.getNombreVues() + 1);
        produitRepository.save(produit);
        
        return DtoMapper.toProduitResponse(produit);
    }
    
    /**
     * Récupérer tous les produits d'un vendeur
     */
    public List<ProduitResponse> getProduitsVendeur(Long vendeurId) {
        Utilisateur vendeur = utilisateurRepository.findById(vendeurId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendeur non trouvé"));
        
        List<Produit> produits = produitRepository.findByVendeurOrderByDateCreationDesc(vendeur);
        
        return produits.stream()
            .map(DtoMapper::toProduitResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les produits d'un vendeur par statut
     */
    public List<ProduitResponse> getProduitsVendeurParStatut(Long vendeurId, StatutProduit statut) {
        Utilisateur vendeur = utilisateurRepository.findById(vendeurId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendeur non trouvé"));
        
        List<Produit> produits = produitRepository.findByVendeurAndStatut(vendeur, statut);
        
        return produits.stream()
            .map(DtoMapper::toProduitResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Marquer un produit comme vendu/indisponible
     */
    @Transactional
    public ProduitResponse marquerCommeVendu(Long produitId, Long vendeurId) {
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        // Vérifier que c'est bien le vendeur du produit
        if (!produit.getVendeur().getId().equals(vendeurId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce produit");
        }
        
        produit.setDisponible(false);
        produit.setDateModification(LocalDateTime.now());
        
        Produit updated = produitRepository.save(produit);
        
        return DtoMapper.toProduitResponse(updated);
    }
    
    /**
     * Archiver un produit
     */
    @Transactional
    public void archiverProduit(Long produitId, Long vendeurId) {
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        // Vérifier que c'est bien le vendeur du produit
        if (!produit.getVendeur().getId().equals(vendeurId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce produit");
        }
        
        produit.setStatut(StatutProduit.ARCHIVE);
        produit.setDisponible(false);
        produit.setDateModification(LocalDateTime.now());
        
        produitRepository.save(produit);
    }
    
    /**
     * Supprimer un produit (avec sa photo)
     */
    @Transactional
    public void supprimerProduit(Long produitId, Long vendeurId) {
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        // Vérifier que c'est bien le vendeur du produit
        if (!produit.getVendeur().getId().equals(vendeurId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer ce produit");
        }
        
        // Supprimer la photo du disque
        String filename = produit.getPhotoUrl().replace("uploads/produits/", "");
        storageService.delete(filename);
        
        // Supprimer le produit de la base de données
        produitRepository.delete(produit);
    }
    
    /**
     * Compter les produits par statut pour un vendeur
     */
    public long compterProduitsParStatut(Long vendeurId, StatutProduit statut) {
        Utilisateur vendeur = utilisateurRepository.findById(vendeurId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendeur non trouvé"));
        
        return produitRepository.countByVendeurAndStatut(vendeur, statut);
    }
    
    /**
     * Récupérer les produits les plus vus
     */
    public List<ProduitResponse> getProduitsPlusVus() {
        List<Produit> produits = produitRepository.findTop10ByStatutOrderByNombreVuesDesc(StatutProduit.APPROUVE);
        
        return produits.stream()
            .map(DtoMapper::toProduitResponse)
            .collect(Collectors.toList());
    }
}
