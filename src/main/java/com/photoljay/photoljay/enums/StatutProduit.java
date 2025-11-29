package com.photoljay.photoljay.enums;

public enum StatutProduit {
    EN_ATTENTE,   // Produit soumis, en attente de modération
    APPROUVE,     // Validé par modérateur, visible publiquement
    REJETE,       // Rejeté par modérateur (photo non conforme, etc.)
    SUSPENDU,     // Suspendu temporairement (signalement, etc.)
    ARCHIVE       // Archivé par le vendeur (produit vendu/retiré)
}