package com.photoljay.photoljay.entity;

import com.photoljay.photoljay.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Destinataire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    // Type et contenu
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeNotification type;
    
    @Column(nullable = false, length = 200)
    private String titre;
    
    @Column(nullable = false, length = 500)
    private String message;
    
    // Lien vers le produit concerné (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id")
    private Produit produit;
    
    // État de lecture
    @Column(nullable = false)
    private Boolean lue = false;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    private LocalDateTime dateLecture;
}
