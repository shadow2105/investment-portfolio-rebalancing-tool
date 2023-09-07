package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_rebalance_config_templates_asset_class")
public class RebalanceConfigTemplateAssetClass extends RebalanceConfigTemplate {

    @Column(name = "percent_cash", nullable = false)
    private double percentCash;

    @Column(name = "percent_equity", nullable = false)
    private double percentEquity;

    @Column(name = "percent_fixed_income", nullable = false)
    private double percentFixedIncome;

    @Column(name = "percent_real_estate", nullable = false)
    private double percentRealEstate;

    @Column(name = "percent_commodity", nullable = false)
    private double percentCommodity;

    @Column(name = "percent_unknown", nullable = false)
    private double percentUnknown;

    public RebalanceConfigTemplateAssetClass() {
    }

    public RebalanceConfigTemplateAssetClass(RebalanceConfigTemplateType type) {
        super(RebalanceConfigTemplateType.ASSET_CLASS);
    }

    public RebalanceConfigTemplateAssetClass(String name, double percentCash, double percentEquity, double percentFixedIncome, double percentRealEstate, double percentCommodity, double percentUnknown) {
        super(name, RebalanceConfigTemplateType.ASSET_CLASS);
        this.percentCash = percentCash;
        this.percentEquity = percentEquity;
        this.percentFixedIncome = percentFixedIncome;
        this.percentRealEstate = percentRealEstate;
        this.percentCommodity = percentCommodity;
        this.percentUnknown = percentUnknown;
    }

    public double getPercentCash() {
        return percentCash;
    }

    public void setPercentCash(double percentCash) {
        this.percentCash = percentCash;
    }

    public double getPercentEquity() {
        return percentEquity;
    }

    public void setPercentEquity(double percentEquity) {
        this.percentEquity = percentEquity;
    }

    public double getPercentFixedIncome() {
        return percentFixedIncome;
    }

    public void setPercentFixedIncome(double percentFixedIncome) {
        this.percentFixedIncome = percentFixedIncome;
    }

    public double getPercentRealEstate() {
        return percentRealEstate;
    }

    public void setPercentRealEstate(double percentRealEstate) {
        this.percentRealEstate = percentRealEstate;
    }

    public double getPercentCommodity() {
        return percentCommodity;
    }

    public void setPercentCommodity(double percentCommodity) {
        this.percentCommodity = percentCommodity;
    }

    public double getPercentUnknown() {
        return percentUnknown;
    }

    public void setPercentUnknown(double percentUnknown) {
        this.percentUnknown = percentUnknown;
    }
}
