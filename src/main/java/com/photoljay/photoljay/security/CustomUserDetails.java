package com.photoljay.photoljay.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.photoljay.photoljay.entity.Utilisateur;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    
    private Long id;
    private String email;
    private String password;
    private String role;
    private boolean actif;
    
    public CustomUserDetails(Long id, String email, String password, String role, boolean actif) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.actif = actif;
    }
    
    public static CustomUserDetails create(Utilisateur utilisateur) {
        return new CustomUserDetails(
            utilisateur.getId(),
            utilisateur.getEmail(),
            utilisateur.getMotDePasse(),
            utilisateur.getRole().name(),
            utilisateur.getActif()
        );
    }
    
    public Long getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getRole() {
        return role;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return actif;
    }
}