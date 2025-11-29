package com.photoljay.photoljay.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.photoljay.photoljay.entity.Notification;
import com.photoljay.photoljay.entity.Produit;
import com.photoljay.photoljay.entity.Utilisateur;
import com.photoljay.photoljay.enums.TypeNotification;
import com.photoljay.photoljay.repository.NotificationRepository;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    /**
     * Crée une notification pour un utilisateur
     */
    @Transactional
    public void creerNotification(Utilisateur utilisateur, TypeNotification type, 
                                   String titre, String message, Produit produit) {
        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setType(type);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setProduit(produit);
        notification.setLue(false);
        notification.setDateCreation(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    /**
     * Notification : produit approuvé
     */
    @Transactional
    public void notifierProduitApprouve(Produit produit) {
        creerNotification(
            produit.getVendeur(),
            TypeNotification.PRODUIT_APPROUVE,
            "Produit approuvé ✓",
            "Votre produit '" + produit.getNom() + "' a été approuvé et est maintenant visible par les acheteurs.",
            produit
        );
    }
    
    /**
     * Notification : produit rejeté
     */
    @Transactional
    public void notifierProduitRejete(Produit produit, String motif) {
        creerNotification(
            produit.getVendeur(),
            TypeNotification.PRODUIT_REJETE,
            "Produit rejeté",
            "Votre produit '" + produit.getNom() + "' a été rejeté. Motif: " + motif,
            produit
        );
    }
    
    /**
     * Notification : nouveau produit à modérer (pour modérateurs)
     */
    @Transactional
    public void notifierNouveauProduitModeration(Produit produit, Utilisateur moderateur) {
        creerNotification(
            moderateur,
            TypeNotification.NOUVEAU_PRODUIT_MODERATION,
            "Nouveau produit à valider",
            "Un nouveau produit '" + produit.getNom() + "' est en attente de modération.",
            produit
        );
    }
    
    /**
     * Marquer une notification comme lue
     */
    @Transactional
    public void marquerCommeLue(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        
        notification.setLue(true);
        notification.setDateLecture(LocalDateTime.now());
        notificationRepository.save(notification);
    }
    
    /**
     * Compter les notifications non lues
     */
    public long compterNonLues(Utilisateur utilisateur) {
        return notificationRepository.countByUtilisateurAndLueFalse(utilisateur);
    }
}