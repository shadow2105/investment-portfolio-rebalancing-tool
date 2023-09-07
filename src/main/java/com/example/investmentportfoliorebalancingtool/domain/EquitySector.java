package com.example.investmentportfoliorebalancingtool.domain;

public enum EquitySector {
    ENERGY("Energy"),
    MATERIALS("Materials"),
    INDUSTRIALS("Industrials"),
    UTILITIES("Utilities"),
    HEALTHCARE("Healthcare"),
    FINANCIALS("Financials"),
    CONSUMER_DISCRETIONARY("Consumer Discretionary"),
    CONSUMER_STAPLES("Consumer Staples"),
    INFORMATION_TECHNOLOGY("Information Technology"),
    COMMUNICATION_SERVICES("Communication Services"),
    REAL_ESTATE("Real Estate"),
    UNKNOWN("Unknown");

    public final String label;

    private EquitySector(String label) {
        this.label = label;
    }
}
