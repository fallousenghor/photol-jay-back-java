package com.photoljay.photoljay.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataPhotoResponse {
    private String modeleAppareil;
    private String fabricantAppareil;
    private LocalDateTime datePrisePhoto;
    private Double latitude;
    private Double longitude;
    private Integer largeur;
    private Integer hauteur;
    private String formatImage;
    private Long tailleOctets;
    private Boolean metadataValides;
    private Boolean priseParAppareil;
    private String logicielUtilise;
}