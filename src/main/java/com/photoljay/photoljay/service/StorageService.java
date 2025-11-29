package com.photoljay.photoljay.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.photoljay.photoljay.exception.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {
    
    private final Path rootLocation;
    
    public StorageService(@Value("${storage.location}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new StorageException("Impossible de créer le dossier de stockage", e);
        }
    }
    
    /**
     * Enregistre un fichier et retourne son nom unique
     */
    public String store(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            if (file.isEmpty()) {
                throw new StorageException("Le fichier est vide: " + originalFilename);
            }
            
            if (originalFilename.contains("..")) {
                throw new StorageException("Le nom de fichier contient une séquence de chemin invalide: " + originalFilename);
            }
            
            // Générer un nom unique pour éviter les conflits
            String extension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            
            // Copier le fichier vers le dossier de destination
            try (InputStream inputStream = file.getInputStream()) {
                Path destinationFile = this.rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath();
                
                if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                    throw new StorageException("Impossible d'enregistrer le fichier en dehors du dossier autorisé");
                }
                
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return uniqueFilename;
            
        } catch (IOException e) {
            throw new StorageException("Échec de l'enregistrement du fichier: " + originalFilename, e);
        }
    }
    
    /**
     * Charge un fichier comme ressource
     */
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }
    
    /**
     * Supprime un fichier
     */
    public void delete(String filename) {
        try {
            Path file = load(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Impossible de supprimer le fichier: " + filename, e);
        }
    }
    
    /**
     * Vérifie si un fichier existe
     */
    public boolean exists(String filename) {
        Path file = load(filename);
        return Files.exists(file);
    }
    
    /**
     * Extrait l'extension du fichier
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
    
    /**
     * Retourne le chemin complet d'un fichier
     */
    public String getFilePath(String filename) {
        return rootLocation.resolve(filename).toString();
    }
}