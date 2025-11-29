package com.photoljay.photoljay.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitResponse {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private String photoUrl;
    private String statut;
    private Boolean disponible;
    private Integer nombreVues;
    
    // Informations de la catégorie
    private Long categorieId;
    private String categorieNom;
    
    // Informations du vendeur
    private Long vendeurId;
    private String vendeurNom;
    private String vendeurTelephone;
    private String vendeurVille;
    
    // Métadonnées photo (pour modérateurs)
    private MetadataPhotoResponse metadataPhoto;
    
    // Modération
    private String motifRejet;
    private LocalDateTime dateModerationDecision;
    
    // Dates
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}