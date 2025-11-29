package com.photoljay.photoljay.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.photoljay.photoljay.entity.HistoriqueModeration;
import com.photoljay.photoljay.entity.Produit;
import com.photoljay.photoljay.entity.Utilisateur;

import java.util.List;

@Repository
public interface HistoriqueModerationRepository extends JpaRepository<HistoriqueModeration, Long> {
    
    // Historique complet d'un produit (ordre chronologique)
    List<HistoriqueModeration> findByProduitOrderByDateActionDesc(Produit produit);
    
    // Toutes les actions d'un modérateur
    List<HistoriqueModeration> findByModerateurOrderByDateActionDesc(Utilisateur moderateur);
    
    // Compter les actions d'un modérateur
    long countByModerateur(Utilisateur moderateur);
    
    // Dernières actions de modération (pour dashboard admin)
    List<HistoriqueModeration> findTop20ByOrderByDateActionDesc();
}
