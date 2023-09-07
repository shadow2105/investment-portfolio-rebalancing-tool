package com.example.investmentportfoliorebalancingtool.domain;

public enum RebalanceConfigTemplateType {
    ASSET_CLASS("Asset Class"),
    COUNTRY("Country"),
    EQUITY_SECTOR("Equity Sector");

    public final String label;

    private RebalanceConfigTemplateType(String label) {
        this.label = label;
    }
}
