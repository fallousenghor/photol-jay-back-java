package com.photoljay.photoljay.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    
    /**
     * Récupère l'utilisateur actuellement connecté
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        
        return null;
    }
    
    /**
     * Récupère l'ID de l'utilisateur connecté
     */
    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getId() : null;
    }
    
    /**
     * Récupère l'email de l'utilisateur connecté
     */
    public static String getCurrentUserEmail() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getEmail() : null;
    }
    
    /**
     * Récupère le rôle de l'utilisateur connecté
     */
    public static String getCurrentUserRole() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getRole() : null;
    }
    
    /**
     * Vérifie si l'utilisateur est connecté
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}