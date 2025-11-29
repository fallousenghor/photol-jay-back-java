package com.photoljay.photoljay.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.photoljay.photoljay.entity.Produit;
import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.enums.StatutProduit;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    // ========== POUR LES ACHETEURS ==========
    
    // Trouver tous les produits approuvés et disponibles (pagination)
    Page<Produit> findByStatutAndDisponibleTrue(StatutProduit statut, Pageable pageable);
    
    // Recherche par catégorie (produits approuvés)
    Page<Produit> findByStatutAndCategorieIdAndDisponibleTrue(
        StatutProduit statut, 
        Long categorieId, 
        Pageable pageable
    );
    
    // Recherche par nom (contient le texte)
    Page<Produit> findByStatutAndNomContainingIgnoreCaseAndDisponibleTrue(
        StatutProduit statut, 
        String nom, 
        Pageable pageable
    );
    
    // ========== POUR LES VENDEURS ==========
    
    // Tous les produits d'un vendeur
    List<Produit> findByVendeurOrderByDateCreationDesc(Utilisateur vendeur);
    
    // Produits d'un vendeur par statut
    List<Produit> findByVendeurAndStatut(Utilisateur vendeur, StatutProduit statut);
    
    // Compter les produits d'un vendeur par statut
    long countByVendeurAndStatut(Utilisateur vendeur, StatutProduit statut);
    
    // ========== POUR LES MODÉRATEURS ==========
    
    // Tous les produits en attente de modération (pagination)
    Page<Produit> findByStatutOrderByDateCreationAsc(StatutProduit statut, Pageable pageable);
    
    // Compter les produits en attente
    long countByStatut(StatutProduit statut);
    
    // Produits récemment modérés par un modérateur
    List<Produit> findByModerateurOrderByDateModerationDecisionDesc(Utilisateur moderateur);
    
    // ========== STATISTIQUES ==========
    
    // Compter tous les produits par statut
    @Query("SELECT p.statut, COUNT(p) FROM Produit p GROUP BY p.statut")
    List<Object[]> countByStatutGrouped();
    
    // Produits les plus consultés
    List<Produit> findTop10ByStatutOrderByNombreVuesDesc(StatutProduit statut);
    
    // Recherche avancée avec plusieurs critères
    @Query("SELECT p FROM Produit p WHERE " +
           "p.statut = :statut AND " +
           "p.disponible = true AND " +
           "(:categorieId IS NULL OR p.categorie.id = :categorieId) AND " +
           "(:recherche IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :recherche, '%')))")
    Page<Produit> rechercheAvancee(
        @Param("statut") StatutProduit statut,
        @Param("categorieId") Long categorieId,
        @Param("recherche") String recherche,
        Pageable pageable
    );
}