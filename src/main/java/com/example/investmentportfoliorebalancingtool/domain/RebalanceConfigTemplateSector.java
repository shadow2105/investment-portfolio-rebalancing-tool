package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_rebalance_config_templates_sector")
public class RebalanceConfigTemplateSector extends RebalanceConfigTemplate {

    @Column(name = "percent_energy", nullable = false)
    private double percentEnergy;

    @Column(name = "percent_materials", nullable = false)
    private double percentMaterials;

    @Column(name = "percent_industrials", nullable = false)
    private double percentIndustrials;

    @Column(name = "percent_utilities", nullable = false)
    private double percentUtilities;

    @Column(name = "percent_healthcare", nullable = false)
    private double percentHealthcare;

    @Column(name = "percent_financials", nullable = false)
    private double percentFinancials;

    @Column(name = "percent_consumer_discretionary", nullable = false)
    private double percentConsumerDiscretionary;

    @Column(name = "percent_consumer_staples", nullable = false)
    private double percentConsumerStaples;

    @Column(name = "percent_information_technology", nullable = false)
    private double percentInformationTechnology;

    @Column(name = "percent_communication_services", nullable = false)
    private double percentCommunicationServices;

    @Column(name = "percent_real_estate", nullable = false)
    private double percentRealEstate;

    @Column(name = "percent_unknown", nullable = false)
    private double percentUnknown;

    public RebalanceConfigTemplateSector() {
    }

    public RebalanceConfigTemplateSector(RebalanceConfigTemplateType type) {
        super(RebalanceConfigTemplateType.EQUITY_SECTOR);
    }

    public RebalanceConfigTemplateSector(String name, double percentEnergy, double percentMaterials, double percentIndustrials, double percentUtilities, double percentHealthcare, double percentFinancials, double percentConsumerDiscretionary, double percentConsumerStaples, double percentInformationTechnology, double percentCommunicationServices, double percentRealEstate, double percentUnknown) {
        super(name, RebalanceConfigTemplateType.EQUITY_SECTOR);
        this.percentEnergy = percentEnergy;
        this.percentMaterials = percentMaterials;
        this.percentIndustrials = percentIndustrials;
        this.percentUtilities = percentUtilities;
        this.percentHealthcare = percentHealthcare;
        this.percentFinancials = percentFinancials;
        this.percentConsumerDiscretionary = percentConsumerDiscretionary;
        this.percentConsumerStaples = percentConsumerStaples;
        this.percentInformationTechnology = percentInformationTechnology;
        this.percentCommunicationServices = percentCommunicationServices;
        this.percentRealEstate = percentRealEstate;
        this.percentUnknown = percentUnknown;
    }

    public double getPercentEnergy() {
        return percentEnergy;
    }

    public void setPercentEnergy(double percentEnergy) {
        this.percentEnergy = percentEnergy;
    }

    public double getPercentMaterials() {
        return percentMaterials;
    }

    public void setPercentMaterials(double percentMaterials) {
        this.percentMaterials = percentMaterials;
    }

    public double getPercentIndustrials() {
        return percentIndustrials;
    }

    public void setPercentIndustrials(double percentIndustrials) {
        this.percentIndustrials = percentIndustrials;
    }

    public double getPercentUtilities() {
        return percentUtilities;
    }

    public void setPercentUtilities(double percentUtilities) {
        this.percentUtilities = percentUtilities;
    }

    public double getPercentHealthcare() {
        return percentHealthcare;
    }

    public void setPercentHealthcare(double percentHealthcare) {
        this.percentHealthcare = percentHealthcare;
    }

    public double getPercentFinancials() {
        return percentFinancials;
    }

    public void setPercentFinancials(double percentFinancials) {
        this.percentFinancials = percentFinancials;
    }

    public double getPercentConsumerDiscretionary() {
        return percentConsumerDiscretionary;
    }

    public void setPercentConsumerDiscretionary(double percentConsumerDiscretionary) {
        this.percentConsumerDiscretionary = percentConsumerDiscretionary;
    }

    public double getPercentConsumerStaples() {
        return percentConsumerStaples;
    }

    public void setPercentConsumerStaples(double percentConsumerStaples) {
        this.percentConsumerStaples = percentConsumerStaples;
    }

    public double getPercentInformationTechnology() {
        return percentInformationTechnology;
    }

    public void setPercentInformationTechnology(double percentInformationTechnology) {
        this.percentInformationTechnology = percentInformationTechnology;
    }

    public double getPercentCommunicationServices() {
        return percentCommunicationServices;
    }

    public void setPercentCommunicationServices(double percentCommunicationServices) {
        this.percentCommunicationServices = percentCommunicationServices;
    }

    public double getPercentRealEstate() {
        return percentRealEstate;
    }

    public void setPercentRealEstate(double percentRealEstate) {
        this.percentRealEstate = percentRealEstate;
    }

    public double getPercentUnknown() {
        return percentUnknown;
    }

    public void setPercentUnknown(double percentUnknown) {
        this.percentUnknown = percentUnknown;
    }
}
