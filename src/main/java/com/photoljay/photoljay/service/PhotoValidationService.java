package com.photoljay.photoljay.service;



import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.photoljay.photoljay.entity.MetadataPhoto;
import com.photoljay.photoljay.exception.PhotoValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class PhotoValidationService {
    
    // Extensions d'images autorisées
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    
    // Taille maximale : 10 MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // Logiciels suspects (éditeurs d'images)
    private static final List<String> SUSPICIOUS_SOFTWARE = Arrays.asList(
        "photoshop", "gimp", "paint.net", "pixlr", "canva", "lightroom"
    );
    
    /**
     * Valide le format et la taille du fichier
     */
    public void validateFileFormat(MultipartFile file) {
        if (file.isEmpty()) {
            throw new PhotoValidationException("Le fichier est vide");
        }
        
        // Vérifier la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new PhotoValidationException("La taille du fichier ne doit pas dépasser 10 MB");
        }
        
        // Vérifier l'extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidExtension(originalFilename)) {
            throw new PhotoValidationException("Format de fichier non autorisé. Utilisez JPG, JPEG ou PNG");
        }
        
        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new PhotoValidationException("Le fichier n'est pas une image valide");
        }
    }
    
    /**
     * Valide l'authenticité de la photo basée sur les métadonnées
     */
    public void validatePhotoAuthenticity(MetadataPhoto metadata) {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();
        
        // Vérifier la présence des métadonnées essentielles
        if (metadata.getModeleAppareil() == null && metadata.getFabricantAppareil() == null) {
            isValid = false;
            errors.append("Aucune information sur l'appareil photo détectée. ");
            metadata.setPriseParAppareil(false);
        }
        
        // Vérifier la date de prise de vue
        if (metadata.getDatePrisePhoto() == null) {
            isValid = false;
            errors.append("Aucune date de prise de vue détectée. ");
            metadata.setPriseParAppareil(false);
        } else {
            // Vérifier que la photo n'est pas dans le futur
            if (metadata.getDatePrisePhoto().isAfter(LocalDateTime.now())) {
                isValid = false;
                errors.append("La date de prise de vue est dans le futur. ");
                metadata.setPriseParAppareil(false);
            }
            
            // Vérifier que la photo n'est pas trop ancienne (plus de 30 jours)
            long daysDifference = ChronoUnit.DAYS.between(metadata.getDatePrisePhoto(), LocalDateTime.now());
            if (daysDifference > 30) {
                isValid = false;
                errors.append("La photo date de plus de 30 jours. Veuillez prendre une photo récente. ");
                metadata.setPriseParAppareil(false);
            }
        }
        
        // Vérifier l'utilisation de logiciels suspects
        if (metadata.getLogicielUtilise() != null) {
            String software = metadata.getLogicielUtilise().toLowerCase();
            for (String suspicious : SUSPICIOUS_SOFTWARE) {
                if (software.contains(suspicious)) {
                    isValid = false;
                    errors.append("La photo semble avoir été éditée avec un logiciel de retouche. ");
                    metadata.setPriseParAppareil(false);
                    break;
                }
            }
        }
        
        metadata.setMetadataValides(isValid);
        
        if (!isValid) {
            metadata.setPriseParAppareil(false);
            throw new PhotoValidationException(
                "Photo non conforme: " + errors.toString() + 
                "Veuillez prendre une nouvelle photo directement avec votre appareil."
            );
        }
        
        metadata.setPriseParAppareil(true);
    }
    
    /**
     * Vérifie si l'extension du fichier est valide
     */
    private boolean hasValidExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }
}
