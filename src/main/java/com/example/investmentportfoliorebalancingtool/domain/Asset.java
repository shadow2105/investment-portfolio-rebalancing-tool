package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@IdClass(AssetId.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_assets")
public class Asset {

    @Id
    @Column(name = "asset_name")
    private String name;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_account_id", referencedColumnName = "id")
    private InvestmentAccount investmentAccount;

    // Specifying only the scale doesn't work; need to specify precision as well
    @Column(name = "number_of_units", nullable = false, precision = 20, scale = 4)
    private BigDecimal numOfUnits;

    @Column(name = "value_per_unit", nullable = false, precision = 20, scale = 3)
    private BigDecimal valuePerUnit;

    @Column(name = "country")
    @Enumerated(value = EnumType.STRING)
    private CountryCodeAlpha2 country;

    @Column(name = "asset_class", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AssetClass assetClass;

    public Asset() {
    }

    public Asset(String name, BigDecimal valuePerUnit, CountryCodeAlpha2 country, AssetClass assetClass) {
        this.name = name;
        this.valuePerUnit = valuePerUnit;
        this.country = country;
        this.assetClass = assetClass;
    }

    public Asset(String name, BigDecimal numOfUnits, BigDecimal valuePerUnit, CountryCodeAlpha2 country, AssetClass assetClass) {
        this.name = name;
        this.numOfUnits = numOfUnits;
        this.valuePerUnit = valuePerUnit;
        this.country = country;
        this.assetClass = assetClass;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InvestmentAccount getInvestmentAccount() {
        return investmentAccount;
    }

    public void setInvestmentAccount(InvestmentAccount investmentAccount) {
        this.investmentAccount = investmentAccount;
    }

    public BigDecimal getNumOfUnits() {
        return numOfUnits;
    }

    public void setNumOfUnits(BigDecimal numOfUnits) {
        this.numOfUnits = numOfUnits;
    }

    public BigDecimal getValuePerUnit() {
        return valuePerUnit;
    }

    public void setValuePerUnit(BigDecimal valuePerUnit) {
        this.valuePerUnit = valuePerUnit;
    }

    public CountryCodeAlpha2 getCountry() {
        return country;
    }

    public void setCountry(CountryCodeAlpha2 country) {
        this.country = country;
    }

    public AssetClass getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(AssetClass assetClass) {
        this.assetClass = assetClass;
    }
}
