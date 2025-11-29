package com.photoljay.photoljay.entity;

import com.photoljay.photoljay.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Identifiants
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, unique = true, length = 20)
    private String telephone;
    
    @Column(nullable = false)
    private String motDePasse;  // Sera hashé avec BCrypt
    
    // Informations personnelles
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(length = 100)
    private String prenom;
    
    // Rôle de l'utilisateur
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.ACHETEUR;  // Par défaut
    
    // Adresse
    @Column(length = 255)
    private String adresse;
    
    @Column(length = 100)
    private String ville;
    
    @Column(length = 100)
    private String pays;
    
    // États du compte
    @Column(nullable = false)
    private Boolean actif = true;
    
    @Column(nullable = false)
    private Boolean emailVerifie = false;
    
    @Column(nullable = false)
    private Boolean telephoneVerifie = false;
    
    // Dates automatiques
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateInscription;
    
    @LastModifiedDate
    private LocalDateTime dateModification;
    
    // Relations
    @OneToMany(mappedBy = "vendeur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> produits = new ArrayList<>();
    
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();
}
