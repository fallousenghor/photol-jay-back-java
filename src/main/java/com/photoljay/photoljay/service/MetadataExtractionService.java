package com.photoljay.photoljay.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.photoljay.photoljay.entity.MetadataPhoto;
import com.photoljay.photoljay.exception.PhotoValidationException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class MetadataExtractionService {
    
    /**
     * Extrait les métadonnées EXIF d'une photo
     */
    public MetadataPhoto extractMetadata(MultipartFile file) {
        MetadataPhoto metadataPhoto = new MetadataPhoto();
        
        try (InputStream inputStream = file.getInputStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
            
            // Informations de l'appareil (EXIF IFD0)
            ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIFD0 != null) {
                metadataPhoto.setModeleAppareil(exifIFD0.getString(ExifIFD0Directory.TAG_MODEL));
                metadataPhoto.setFabricantAppareil(exifIFD0.getString(ExifIFD0Directory.TAG_MAKE));
                metadataPhoto.setLogicielUtilise(exifIFD0.getString(ExifIFD0Directory.TAG_SOFTWARE));
            }
            
            // Informations de prise de vue (EXIF SubIFD)
            ExifSubIFDDirectory exifSubIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifSubIFD != null) {
                Date dateOriginal = exifSubIFD.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (dateOriginal != null) {
                    metadataPhoto.setDatePrisePhoto(
                        LocalDateTime.ofInstant(dateOriginal.toInstant(), ZoneId.systemDefault())
                    );
                }
                
                metadataPhoto.setLargeur(exifSubIFD.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH));
                metadataPhoto.setHauteur(exifSubIFD.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
            }
            
            // Informations GPS
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                var geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null) {
                    metadataPhoto.setLatitude(geoLocation.getLatitude());
                    metadataPhoto.setLongitude(geoLocation.getLongitude());
                }
            }
            
            // Informations générales du fichier
            metadataPhoto.setFormatImage(file.getContentType());
            metadataPhoto.setTailleOctets(file.getSize());
            
            // Stocker toutes les métadonnées en JSON (pour référence)
            metadataPhoto.setMetadataCompletesJson(getAllMetadataAsJson(metadata));
            
        } catch (Exception e) {
            throw new PhotoValidationException("Impossible d'extraire les métadonnées de la photo", e);
        }
        
        return metadataPhoto;
    }
    
    /**
     * Convertit toutes les métadonnées en JSON
     */
    private String getAllMetadataAsJson(Metadata metadata) {
        StringBuilder json = new StringBuilder("{");
        
        for (Directory directory : metadata.getDirectories()) {
            json.append("\"").append(directory.getName()).append("\": {");
            
            directory.getTags().forEach(tag -> {
                json.append("\"").append(tag.getTagName()).append("\": \"")
                    .append(tag.getDescription().replace("\"", "\\\"")).append("\",");
            });
            
            // Retirer la dernière virgule
            if (json.charAt(json.length() - 1) == ',') {
                json.deleteCharAt(json.length() - 1);
            }
            
            json.append("},");
        }
        
        // Retirer la dernière virgule
        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }
        
        json.append("}");
        
        return json.toString();
    }
}