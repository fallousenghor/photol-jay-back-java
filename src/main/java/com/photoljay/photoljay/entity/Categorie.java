package com.photoljay.photoljay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categorie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String nom;  // Ex: "Électronique", "Vêtements", "Meubles"
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 255)
    private String icone;  // URL ou nom de fichier d'icône
    
    @Column(nullable = false)
    private Boolean active = true;  // Permet de désactiver une catégorie
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    // Relations
    @OneToMany(mappedBy = "categorie")
    private List<Produit> produits = new ArrayList<>();
}