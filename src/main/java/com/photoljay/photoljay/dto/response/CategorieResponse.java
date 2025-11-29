package com.photoljay.photoljay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorieResponse {
    private Long id;
    private String nom;
    private String description;
    private String icone;
    private Boolean active;
    private Integer nombreProduits; // Nombre de produits dans cette catégorie
    private LocalDateTime dateCreation;
}