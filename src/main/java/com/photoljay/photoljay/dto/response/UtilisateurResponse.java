package com.photoljay.photoljay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponse {
    private Long id;
    private String email;
    private String telephone;
    private String nom;
    private String prenom;
    private String role;
    private String adresse;
    private String ville;
    private String pays;
    private Boolean actif;
    private Boolean emailVerifie;
    private Boolean telephoneVerifie;
    private LocalDateTime dateInscription;
}
