package com.example.investmentportfoliorebalancingtool.domain;

public enum AssetClass {
    CASH("Cash"),           // Cash, Cash Equivalents, Money Market Funds, Treasury Bills
    EQUITY("Equity"),         // Shares, ETFs, Mutual Funds, REITs
    FIXED_INCOME("Fixed Income"),   // Bonds, GICs or CDs
    REAL_ESTATE("Real Estate"),    // Physical Real Estate
    COMMODITY("Commodity"),       // Metals, Energy, Livestock, Agriculture Commodities
    UNKNOWN("Unknown");

    public final String label;

    private AssetClass(String label) {
        this.label = label;
    }
}
