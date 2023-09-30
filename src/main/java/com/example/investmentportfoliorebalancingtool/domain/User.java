package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "registered_users")
public class User extends BaseEntityAudit implements Persistable<UUID> {

    @Column(name = "given_name", nullable = false)
    private String givenName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "username", unique = true, nullable = false, updatable = false)
    private String userName;

    @Column(name = "email", unique = true, nullable = false, updatable = false)
    private String email;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "locale")
    private String locale;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private UserProfile userProfile;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private AdminProfile adminProfile;

    @Transient
    private boolean isNew = true;

    @PostLoad
    @PrePersist
    public void trackNotNew() {
        this.isNew = false;
    }

    public User() {
    }

    public User(String givenName, String middleName, String familyName, String userName, String email, String profilePicture, String locale, UserRole role) {
        this.givenName = givenName;
        this.middleName = middleName;
        this.familyName = familyName;
        this.userName = userName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.locale = locale;
        this.role = role;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public AdminProfile getAdminProfile() {
        return adminProfile;
    }

    public void setAdminProfile(AdminProfile adminProfile) {
        this.adminProfile = adminProfile;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
