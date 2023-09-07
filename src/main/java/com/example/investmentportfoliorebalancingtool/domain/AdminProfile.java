package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_profiles")
public class AdminProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false, nullable = false)
    private User user;

    public AdminProfile() {
    }

    public AdminProfile(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
