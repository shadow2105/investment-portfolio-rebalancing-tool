package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_rebalance_config_templates_country")
public class RebalanceConfigTemplateCountry extends RebalanceConfigTemplate {

    @Column(name = "percent_ca", nullable = false)
    private double percentCA;

    @Column(name = "percent_us", nullable = false)
    private double percentUS;

    @Column(name = "percent_unknown", nullable = false)
    private double percentUnknown;

    public RebalanceConfigTemplateCountry() {
    }

    public RebalanceConfigTemplateCountry(RebalanceConfigTemplateType type) {
        super(RebalanceConfigTemplateType.COUNTRY);
    }

    public RebalanceConfigTemplateCountry(String name, double percentCA, double percentUS, double percentUnknown) {
        super(name, RebalanceConfigTemplateType.COUNTRY);
        this.percentCA = percentCA;
        this.percentUS = percentUS;
        this.percentUnknown = percentUnknown;
    }

    public double getPercentCA() {
        return percentCA;
    }

    public void setPercentCA(double percentCA) {
        this.percentCA = percentCA;
    }

    public double getPercentUS() {
        return percentUS;
    }

    public void setPercentUS(double percentUS) {
        this.percentUS = percentUS;
    }

    public double getPercentUnknown() {
        return percentUnknown;
    }

    public void setPercentUnknown(double percentUnknown) {
        this.percentUnknown = percentUnknown;
    }
}
