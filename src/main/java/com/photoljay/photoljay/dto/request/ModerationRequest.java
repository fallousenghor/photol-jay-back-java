package com.photoljay.photoljay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationRequest {
    
    @NotBlank(message = "L'action est obligatoire")
    private String action; // "APPROUVER" ou "REJETER"
    
    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
    private String commentaire; // Optionnel pour approbation, obligatoire pour rejet
}