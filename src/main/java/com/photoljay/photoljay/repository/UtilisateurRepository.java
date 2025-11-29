package com.photoljay.photoljay.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    // Recherche par email (pour login)
    Optional<Utilisateur> findByEmail(String email);
    
    // Recherche par téléphone
    Optional<Utilisateur> findByTelephone(String telephone);
    
    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);
    
    // Vérifier si un téléphone existe déjà
    boolean existsByTelephone(String telephone);
    
    // Trouver tous les utilisateurs par rôle
    List<Utilisateur> findByRole(Role role);
    
    // Trouver les utilisateurs actifs
    List<Utilisateur> findByActifTrue();
    
    // Trouver les modérateurs actifs
    List<Utilisateur> findByRoleAndActifTrue(Role role);
}