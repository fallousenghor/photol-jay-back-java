package com.photoljay.photoljay.entity;

import com.photoljay.photoljay.enums.ActionModeration;
import com.photoljay.photoljay.enums.StatutProduit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_moderation")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueModeration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderateur_id", nullable = false)
    private Utilisateur moderateur;
    
    // Action effectuée
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActionModeration action;
    
    @Column(length = 1000)
    private String commentaire;  // Commentaire du modérateur
    
    // Changement de statut
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutProduit statutAvant;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutProduit statutApres;
    
    // Date de l'action
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateAction;
}
