package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@IdClass(RebalanceConfigTemplateId.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_rebalance_config_templates")
public class RebalanceConfigTemplate {

    @Id
    @Column(name = "template_name")
    private String name;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    private UserProfile userProfile;

    @Column(name = "template_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RebalanceConfigTemplateType type;

    @Column(name = "created_by", updatable = false)
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public RebalanceConfigTemplate() {
    }

    public RebalanceConfigTemplate(RebalanceConfigTemplateType type) {
        this.type = type;
    }

    public RebalanceConfigTemplate(String name, RebalanceConfigTemplateType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public RebalanceConfigTemplateType getType() {
        return type;
    }

    public void setType(RebalanceConfigTemplateType type) {
        this.type = type;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
