package com.photoljay.photoljay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String type;
    private String titre;
    private String message;
    private Long produitId;
    private String produitNom;
    private Boolean lue;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
}
