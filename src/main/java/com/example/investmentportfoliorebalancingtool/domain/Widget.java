package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dashboard_widgets")
public class Widget extends BaseEntity {

    @Column(name = "widget_description", nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private WidgetDescription description;

    @Transient
    private Object dataObj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id", nullable = false, updatable = false)
    private UserProfile userProfile;

    public Widget() {
    }

    public Widget(WidgetDescription description) {
        this.description = description;
    }

    public WidgetDescription getDescription() {
        return description;
    }

    public void setDescription(WidgetDescription description) {
        this.description = description;
    }

    public Object getDataObj() {
        return dataObj;
    }

    public void setDataObj(Object dataObj) {
        this.dataObj = dataObj;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
