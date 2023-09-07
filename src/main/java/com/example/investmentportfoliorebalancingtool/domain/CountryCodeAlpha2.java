package com.example.investmentportfoliorebalancingtool.domain;

public enum CountryCodeAlpha2 {
    CA("Canada"),
    US("United States of America"),
    UNKNOWN("Unknown");

    public final String label;

    private CountryCodeAlpha2(String label) {
        this.label = label;
    }
}
