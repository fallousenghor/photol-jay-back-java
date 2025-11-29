package com.photoljay.photoljay.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.photoljay.photoljay.entity.Notification;
import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.enums.TypeNotification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Toutes les notifications d'un utilisateur (pagination)
    Page<Notification> findByUtilisateurOrderByDateCreationDesc(Utilisateur utilisateur, Pageable pageable);
    
    // Notifications non lues d'un utilisateur
    List<Notification> findByUtilisateurAndLueFalseOrderByDateCreationDesc(Utilisateur utilisateur);
    
    // Compter les notifications non lues
    long countByUtilisateurAndLueFalse(Utilisateur utilisateur);
    
    // Notifications par type
    List<Notification> findByUtilisateurAndTypeOrderByDateCreationDesc(
        Utilisateur utilisateur, 
        TypeNotification type
    );
    
    // Supprimer toutes les notifications lues d'un utilisateur
    void deleteByUtilisateurAndLueTrue(Utilisateur utilisateur);
}
