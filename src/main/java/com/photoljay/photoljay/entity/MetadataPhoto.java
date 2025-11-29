package com.photoljay.photoljay.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataPhoto {
    
    // Informations sur l'appareil
    private String modeleAppareil;      // Ex: "iPhone 14 Pro", "Samsung Galaxy S23"
    private String fabricantAppareil;   // Ex: "Apple", "Samsung"
    
    // Date et heure de prise de vue
    @Column(name = "date_prise_photo")
    private LocalDateTime datePrisePhoto;
    
    // Localisation GPS (si disponible)
    private Double latitude;
    private Double longitude;
    
    // Caractéristiques de l'image
    private Integer largeur;            // En pixels
    private Integer hauteur;            // En pixels
    private String formatImage;         // "JPEG", "PNG", etc.
    private Long tailleOctets;          // Taille du fichier en bytes
    
    // Validations
    @Column(nullable = false)
    private Boolean metadataValides = false;     // Les métadonnées sont présentes
    
    @Column(nullable = false)
    private Boolean priseParAppareil = false;    // Photo prise par un appareil (pas téléchargée)
    
    // Détection de manipulation
    private String logicielUtilise;     // Ex: "Photoshop" = suspect
    
    // Stockage complet des métadonnées (JSON)
    @Column(length = 2000)
    private String metadataCompletesJson;
}