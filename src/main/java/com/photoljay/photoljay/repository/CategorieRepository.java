package com.photoljay.photoljay.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.photoljay.photoljay.entity.Categorie;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    
    // Recherche par nom
    Optional<Categorie> findByNom(String nom);
    
    // Vérifier si une catégorie existe
    boolean existsByNom(String nom);
    
    // Trouver toutes les catégories actives
    List<Categorie> findByActiveTrue();
    
    // Trouver par nom (insensible à la casse)
    Optional<Categorie> findByNomIgnoreCase(String nom);
}
