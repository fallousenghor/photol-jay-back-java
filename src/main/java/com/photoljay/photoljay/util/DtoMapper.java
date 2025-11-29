package com.photoljay.photoljay.util;

import com.photoljay.photoljay.dto.response.CategorieResponse;
import com.photoljay.photoljay.dto.response.MetadataPhotoResponse;
import com.photoljay.photoljay.dto.response.NotificationResponse;
import com.photoljay.photoljay.dto.response.ProduitResponse;
import com.photoljay.photoljay.dto.response.UtilisateurResponse;
import com.photoljay.photoljay.entity.Categorie;
import com.photoljay.photoljay.entity.MetadataPhoto;
import com.photoljay.photoljay.entity.Notification;
import com.photoljay.photoljay.entity.Produit;
import com.photoljay.photoljay.entity.Utilisateur;

public class DtoMapper {
    
    // Utilisateur → UtilisateurResponse
    public static UtilisateurResponse toUtilisateurResponse(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        
        return new UtilisateurResponse(
            utilisateur.getId(),
            utilisateur.getEmail(),
            utilisateur.getTelephone(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getRole().name(),
            utilisateur.getAdresse(),
            utilisateur.getVille(),
            utilisateur.getPays(),
            utilisateur.getActif(),
            utilisateur.getEmailVerifie(),
            utilisateur.getTelephoneVerifie(),
            utilisateur.getDateInscription()
        );
    }
    
    // Produit → ProduitResponse
    public static ProduitResponse toProduitResponse(Produit produit) {
        if (produit == null) return null;
        
        ProduitResponse response = new ProduitResponse();
        response.setId(produit.getId());
        response.setNom(produit.getNom());
        response.setDescription(produit.getDescription());
        response.setPrix(produit.getPrix());
        response.setPhotoUrl(produit.getPhotoUrl());
        response.setStatut(produit.getStatut().name());
        response.setDisponible(produit.getDisponible());
        response.setNombreVues(produit.getNombreVues());
        
        // Catégorie
        if (produit.getCategorie() != null) {
            response.setCategorieId(produit.getCategorie().getId());
            response.setCategorieNom(produit.getCategorie().getNom());
        }
        
        // Vendeur
        if (produit.getVendeur() != null) {
            response.setVendeurId(produit.getVendeur().getId());
            response.setVendeurNom(produit.getVendeur().getNom() + " " + 
                                   (produit.getVendeur().getPrenom() != null ? produit.getVendeur().getPrenom() : ""));
            response.setVendeurTelephone(produit.getVendeur().getTelephone());
            response.setVendeurVille(produit.getVendeur().getVille());
        }
        
        // Métadonnées photo
        if (produit.getMetadataPhoto() != null) {
            response.setMetadataPhoto(toMetadataPhotoResponse(produit.getMetadataPhoto()));
        }
        
        // Modération
        response.setMotifRejet(produit.getMotifRejet());
        response.setDateModerationDecision(produit.getDateModerationDecision());
        
        // Dates
        response.setDateCreation(produit.getDateCreation());
        response.setDateModification(produit.getDateModification());
        
        return response;
    }
    
    // MetadataPhoto → MetadataPhotoResponse
    public static MetadataPhotoResponse toMetadataPhotoResponse(MetadataPhoto metadata) {
        if (metadata == null) return null;
        
        return new MetadataPhotoResponse(
            metadata.getModeleAppareil(),
            metadata.getFabricantAppareil(),
            metadata.getDatePrisePhoto(),
            metadata.getLatitude(),
            metadata.getLongitude(),
            metadata.getLargeur(),
            metadata.getHauteur(),
            metadata.getFormatImage(),
            metadata.getTailleOctets(),
            metadata.getMetadataValides(),
            metadata.getPriseParAppareil(),
            metadata.getLogicielUtilise()
        );
    }
    
    // Categorie → CategorieResponse
    public static CategorieResponse toCategorieResponse(Categorie categorie) {
        if (categorie == null) return null;
        
        return new CategorieResponse(
            categorie.getId(),
            categorie.getNom(),
            categorie.getDescription(),
            categorie.getIcone(),
            categorie.getActive(),
            categorie.getProduits() != null ? categorie.getProduits().size() : 0,
            categorie.getDateCreation()
        );
    }
    
    // Notification → NotificationResponse
    public static NotificationResponse toNotificationResponse(Notification notification) {
        if (notification == null) return null;
        
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(notification.getType().name());
        response.setTitre(notification.getTitre());
        response.setMessage(notification.getMessage());
        
        if (notification.getProduit() != null) {
            response.setProduitId(notification.getProduit().getId());
            response.setProduitNom(notification.getProduit().getNom());
        }
        
        response.setLue(notification.getLue());
        response.setDateCreation(notification.getDateCreation());
        response.setDateLecture(notification.getDateLecture());
        
        return response;
    }
}