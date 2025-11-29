package com.photoljay.photoljay.entity;

import com.photoljay.photoljay.enums.StatutProduit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produits")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Informations du produit
    @Column(nullable = false, length = 200)
    private String nom;
    
    @Column(nullable = false, length = 2000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;  // Utilise BigDecimal pour l'argent (précision)
    
    // Photo (obligatoire et prise avec appareil)
    @Column(nullable = false, length = 500)
    private String photoUrl;  // Chemin du fichier: "uploads/produits/abc123.jpg"
    
    // Statut du produit
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutProduit statut = StatutProduit.EN_ATTENTE;
    
    // Métadonnées de la photo (intégré)
    @Embedded
    private MetadataPhoto metadataPhoto;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendeur_id", nullable = false)
    private Utilisateur vendeur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;
    
    // Modération
    @Column(length = 1000)
    private String motifRejet;  // Raison du rejet si REJETE
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderateur_id")
    private Utilisateur moderateur;  // Qui a modéré
    
    private LocalDateTime dateModerationDecision;  // Quand a été modéré
    
    // Dates
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    @LastModifiedDate
    private LocalDateTime dateModification;
    
    // Statistiques
    @Column(nullable = false)
    private Integer nombreVues = 0;
    
    @Column(nullable = false)
    private Boolean disponible = true;  // Vendeur peut marquer comme vendu
    
    // Historique des actions de modération
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<HistoriqueModeration> historiqueModeration = new ArrayList<>();
}