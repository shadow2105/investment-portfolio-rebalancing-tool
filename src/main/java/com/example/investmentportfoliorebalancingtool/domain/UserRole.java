package com.example.investmentportfoliorebalancingtool.domain;

public enum UserRole {
    USER("User"),
    ADMIN("Admin");

    public final String label;

    private UserRole(String label) {
        this.label = label;
    }
}
