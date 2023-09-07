package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RebalanceConfigTemplateService {

    RebalanceConfigTemplate addRebalancingTemplate(User user, RebalanceConfigTemplate rebalanceConfigTemplate);

    Optional<RebalanceConfigTemplate> getRebalancingTemplateFromName(User user, String templateName);

    Set<RebalanceConfigTemplate> getRebalancingTemplates(User user);

    Set<RebalanceConfigTemplate> getAssetClassRebalancingTemplates(User user);

    Set<RebalanceConfigTemplate> getCountryRebalancingTemplates(User user);

    Set<RebalanceConfigTemplate> getSectorRebalancingTemplates(User user);

    void removeRebalancingTemplateByName(User user, String templateName);


    // For Individual Tools
    Map<AssetClass, Double> getTemplateAssetClassAllocation(RebalanceConfigTemplateAssetClass templateAssetClass);

    Map<CountryCodeAlpha2, Double> getTemplateCountryAllocation(RebalanceConfigTemplateCountry templateCountry);

    Map<EquitySector, Double> getTemplateSectorAllocation(RebalanceConfigTemplateSector templateSector);


    // For Widgets

    // ASSET_CLASS_BASED_REBALANCING_TEMPLATES
    // {TemplateName1: {CASH: %, EQUITY: %, ... }, TemplateName1: {CASH: %, EQUITY: %, ... }, ... }
    Map<String, Map<AssetClass, Double>> getAllTemplatesAssetClassAllocation(User user);

    // COUNTRY_BASED_REBALANCING_TEMPLATES
    // {TemplateName1: {CA: %, US: %, ... }, TemplateName1: {CA: %, US: %, ... }, ... }
    Map<String, Map<CountryCodeAlpha2, Double>> getAllTemplatesCountryAllocation(User user);

    // SECTOR_BASED_REBALANCING_TEMPLATES
    // {TemplateName1: {Energy: %, Financials: %, ... }, TemplateName1: {Energy: %, Financials: %, ... }, ... }
    Map<String, Map<EquitySector, Double>> getAllTemplatesSectorAllocation(User user);


    // For Help
    int numberOfRebalancingTemplates(User user);

    boolean existsForUser(User user, String templateName);

    RebalanceConfigTemplateAssetClass validateAssetClassTemplateAndSetUnknownAllocation(RebalanceConfigTemplateAssetClass templateAssetClass);

    RebalanceConfigTemplateCountry validateCountryTemplateAndSetUnknownAllocation(RebalanceConfigTemplateCountry templateCountry);

    RebalanceConfigTemplateSector validateSectorTemplateAndSetUnknownAllocation(RebalanceConfigTemplateSector templateSector);
}
