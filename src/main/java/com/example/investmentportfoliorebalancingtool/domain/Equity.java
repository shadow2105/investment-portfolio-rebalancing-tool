package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_assets_equities")
public class Equity extends Asset {

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "sector")
    @Enumerated(value = EnumType.STRING)
    private EquitySector sector;

    @Column(name = "industry")
    private String industry;

    public Equity() {
    }

    // For ETFs
    public Equity(String name, BigDecimal valuePerUnit, CountryCodeAlpha2 country, String symbol) {
        super(name, valuePerUnit, country, AssetClass.EQUITY);
        this.symbol = symbol;
        this.sector = EquitySector.UNKNOWN;
    }

    // For Shares
    public Equity(String name, BigDecimal numOfUnits, BigDecimal valuePerUnit, CountryCodeAlpha2 country, String symbol, String companyName, EquitySector sector, String industry) {
        super(name, numOfUnits, valuePerUnit, country, AssetClass.EQUITY);
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
        this.industry = industry;
    }

    public Equity(String name, BigDecimal valuePerUnit, CountryCodeAlpha2 country, String symbol, String companyName, EquitySector sector, String industry) {
        super(name, valuePerUnit, country, AssetClass.EQUITY);
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
        this.industry = industry;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public EquitySector getSector() {
        return sector;
    }

    public void setSector(EquitySector sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
