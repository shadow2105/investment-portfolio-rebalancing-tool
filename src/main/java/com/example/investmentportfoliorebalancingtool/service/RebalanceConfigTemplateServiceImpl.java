package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RebalanceConfigTemplateServiceImpl implements RebalanceConfigTemplateService {

    @Override
    @Transactional
    public RebalanceConfigTemplate addRebalancingTemplate(User user, RebalanceConfigTemplate rebalancingTemplate) {
        rebalancingTemplate.setCreatedBy(user.getUserName());
        rebalancingTemplate.setUpdatedBy(user.getUserName());
        return user.getUserProfile().addRebalanceConfigTemplate(rebalancingTemplate);
    }

    @Override
    public Optional<RebalanceConfigTemplate> getRebalancingTemplateFromName(User user, String templateName) {
        return user.getUserProfile()
                .getRebalanceConfigTemplates().stream()
                .filter(template -> template.getName().equals(templateName))
                .findFirst();
    }

    @Override
    @Transactional
    public Set<RebalanceConfigTemplate> getRebalancingTemplates(User user) {
        // log
        Set<RebalanceConfigTemplate> rebalanceConfigTemplates = Collections.synchronizedSet(new LinkedHashSet<>());
        // Iterator is not preserving order even after using a LinkedHashSet
        user.getUserProfile().getRebalanceConfigTemplates().iterator().forEachRemaining(rebalanceConfigTemplates::add);
        return rebalanceConfigTemplates;
    }

    @Override
    public Set<RebalanceConfigTemplate> getAssetClassRebalancingTemplates(User user) {
        // log
        return user.getUserProfile()
                .getRebalanceConfigTemplates().stream()
                .filter(template -> template.getType().equals(RebalanceConfigTemplateType.ASSET_CLASS))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<RebalanceConfigTemplate> getCountryRebalancingTemplates(User user) {
        // log
        return user.getUserProfile()
                .getRebalanceConfigTemplates().stream()
                .filter(template -> template.getType().equals(RebalanceConfigTemplateType.COUNTRY))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<RebalanceConfigTemplate> getSectorRebalancingTemplates(User user) {
        // log
        return user.getUserProfile()
                .getRebalanceConfigTemplates().stream()
                .filter(template -> template.getType().equals(RebalanceConfigTemplateType.EQUITY_SECTOR))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @Transactional
    public void removeRebalancingTemplateByName(User user, String templateName) {
        for(RebalanceConfigTemplate rebalancingTemplate: user.getUserProfile().getRebalanceConfigTemplates()) {
            if (rebalancingTemplate.getName().equals(templateName)) {
                user.getUserProfile().removeRebalanceConfigTemplate(rebalancingTemplate);
                return;
            }
        }
    }

    @Override
    public Map<AssetClass, Double> getTemplateAssetClassAllocation(RebalanceConfigTemplateAssetClass templateAssetClass) {
        Map<AssetClass, Double> allocationMap = Collections.synchronizedMap(new LinkedHashMap<>());
        allocationMap.put(AssetClass.CASH, templateAssetClass.getPercentCash());
        allocationMap.put(AssetClass.EQUITY, templateAssetClass.getPercentEquity());
        allocationMap.put(AssetClass.FIXED_INCOME, templateAssetClass.getPercentFixedIncome());
        allocationMap.put(AssetClass.REAL_ESTATE, templateAssetClass.getPercentRealEstate());
        allocationMap.put(AssetClass.COMMODITY, templateAssetClass.getPercentCommodity());
        allocationMap.put(AssetClass.UNKNOWN, templateAssetClass.getPercentUnknown());

        return allocationMap;
    }

    @Override
    public Map<CountryCodeAlpha2, Double> getTemplateCountryAllocation(RebalanceConfigTemplateCountry templateCountry) {
        Map<CountryCodeAlpha2, Double> allocationMap = Collections.synchronizedMap(new LinkedHashMap<>());
        allocationMap.put(CountryCodeAlpha2.CA, templateCountry.getPercentCA());
        allocationMap.put(CountryCodeAlpha2.US, templateCountry.getPercentUS());
        allocationMap.put(CountryCodeAlpha2.UNKNOWN, templateCountry.getPercentUnknown());

        return allocationMap;
    }

    @Override
    public Map<EquitySector, Double> getTemplateSectorAllocation(RebalanceConfigTemplateSector templateSector) {
        Map<EquitySector, Double> allocationMap = Collections.synchronizedMap(new LinkedHashMap<>());
        allocationMap.put(EquitySector.ENERGY, templateSector.getPercentEnergy());
        allocationMap.put(EquitySector.MATERIALS, templateSector.getPercentMaterials());
        allocationMap.put(EquitySector.INDUSTRIALS, templateSector.getPercentIndustrials());
        allocationMap.put(EquitySector.UTILITIES, templateSector.getPercentUtilities());
        allocationMap.put(EquitySector.HEALTHCARE, templateSector.getPercentHealthcare());
        allocationMap.put(EquitySector.FINANCIALS, templateSector.getPercentFinancials());
        allocationMap.put(EquitySector.CONSUMER_DISCRETIONARY, templateSector.getPercentConsumerDiscretionary());
        allocationMap.put(EquitySector.CONSUMER_STAPLES, templateSector.getPercentConsumerStaples());
        allocationMap.put(EquitySector.INFORMATION_TECHNOLOGY, templateSector.getPercentInformationTechnology());
        allocationMap.put(EquitySector.COMMUNICATION_SERVICES, templateSector.getPercentCommunicationServices());
        allocationMap.put(EquitySector.REAL_ESTATE, templateSector.getPercentRealEstate());
        allocationMap.put(EquitySector.UNKNOWN, templateSector.getPercentUnknown());

        return allocationMap;
    }

    @Override
    @Transactional
    public Map<String, Map<AssetClass, Double>> getAllTemplatesAssetClassAllocation(User user) {
        Map<String, Map<AssetClass, Double>> userTemplatesAssetClassAllocation= Collections.synchronizedMap(new LinkedHashMap<>());

        for(RebalanceConfigTemplate template: user.getUserProfile().getRebalanceConfigTemplates()) {
            if (template instanceof RebalanceConfigTemplateAssetClass) {
                userTemplatesAssetClassAllocation.put(
                        template.getName(),
                        getTemplateAssetClassAllocation((RebalanceConfigTemplateAssetClass) template)
                );
            }
        }

        return userTemplatesAssetClassAllocation;
    }

    @Override
    @Transactional
    public Map<String, Map<CountryCodeAlpha2, Double>> getAllTemplatesCountryAllocation(User user) {
        Map<String, Map<CountryCodeAlpha2, Double>> userTemplatesCountryAllocation = Collections.synchronizedMap(new LinkedHashMap<>());

        for(RebalanceConfigTemplate template: user.getUserProfile().getRebalanceConfigTemplates()) {
            if (template instanceof RebalanceConfigTemplateCountry) {
                userTemplatesCountryAllocation.put(
                        template.getName(),
                        getTemplateCountryAllocation((RebalanceConfigTemplateCountry) template)
                );
            }
        }

        return userTemplatesCountryAllocation;
    }

    @Override
    @Transactional
    public Map<String, Map<EquitySector, Double>> getAllTemplatesSectorAllocation(User user) {
        Map<String, Map<EquitySector, Double>> userTemplatesSectorAllocation = Collections.synchronizedMap(new LinkedHashMap<>());

        for(RebalanceConfigTemplate template: user.getUserProfile().getRebalanceConfigTemplates()) {
            if (template instanceof RebalanceConfigTemplateSector) {
                userTemplatesSectorAllocation.put(
                        template.getName(),
                        getTemplateSectorAllocation((RebalanceConfigTemplateSector) template)
                );
            }
        }

        return userTemplatesSectorAllocation;
    }

    @Override
    public int numberOfRebalancingTemplates(User user) {
        return user.getUserProfile().getRebalanceConfigTemplates().size();
    }

    @Override
    public boolean existsForUser(User user, String templateName) {
        for(RebalanceConfigTemplate rebalancingTemplate: user.getUserProfile().getRebalanceConfigTemplates()) {
            if (rebalancingTemplate.getName().equalsIgnoreCase(templateName)) {
                return true;
            }
        }

        return false;
    }

    // Can use Reflection instead of having different methods for each template type
    /*
    for(Field field : templateObj.getClass().getDeclaredFields()) {
            Class type = field.getType();
            if (type == double.class) {
                double value = field.getDouble(templateObj);
                //do something;
            }
     }

     */
    @Override
    @Transactional
    public RebalanceConfigTemplateAssetClass validateAssetClassTemplateAndSetUnknownAllocation(RebalanceConfigTemplateAssetClass templateAssetClass) {
        double totalAllocation = templateAssetClass.getPercentCash() +
                templateAssetClass.getPercentEquity() +
                templateAssetClass.getPercentFixedIncome() +
                templateAssetClass.getPercentRealEstate() +
                templateAssetClass.getPercentCommodity();

        if(totalAllocation > 100.0) {
            throw new RuntimeException("Invalid Asset Class Template!\n" +
                    "Total Allocation cannot exceed 100.00 percent .");
        }
        else {
            BigDecimal unknown = BigDecimal.valueOf(100.00 - totalAllocation);
            unknown = unknown.setScale(2, RoundingMode.HALF_UP);

            templateAssetClass.setPercentUnknown(unknown.doubleValue());
        }

        return templateAssetClass;
    }

    @Override
    @Transactional
    public RebalanceConfigTemplateCountry validateCountryTemplateAndSetUnknownAllocation(RebalanceConfigTemplateCountry templateCountry) {
        double totalAllocation = templateCountry.getPercentCA() +
                templateCountry.getPercentUS();

        if(totalAllocation > 100.0) {
            throw new RuntimeException("Invalid Country Template!\n" +
                    "Total Allocation cannot exceed 100.00 percent .");
        }
        else {
            BigDecimal unknown = BigDecimal.valueOf(100.00 - totalAllocation);
            unknown = unknown.setScale(2, RoundingMode.HALF_UP);

            templateCountry.setPercentUnknown(unknown.doubleValue());
        }

        return templateCountry;
    }

    @Override
    @Transactional
    public RebalanceConfigTemplateSector validateSectorTemplateAndSetUnknownAllocation(RebalanceConfigTemplateSector templateSector) {
        double totalAllocation = templateSector.getPercentEnergy() +
                templateSector.getPercentMaterials() +
                templateSector.getPercentIndustrials() +
                templateSector.getPercentUtilities() +
                templateSector.getPercentHealthcare() +
                templateSector.getPercentFinancials() +
                templateSector.getPercentConsumerDiscretionary() +
                templateSector.getPercentConsumerStaples() +
                templateSector.getPercentInformationTechnology() +
                templateSector.getPercentCommunicationServices() +
                templateSector.getPercentRealEstate();

        if(totalAllocation > 100.0) {
            throw new RuntimeException("Invalid Sector Template!\n" +
                    "Total Allocation cannot exceed 100.00 percent .");
        }
        else {
            BigDecimal unknown = BigDecimal.valueOf(100.00 - totalAllocation);
            unknown = unknown.setScale(2, RoundingMode.HALF_UP);

            templateSector.setPercentUnknown(unknown.doubleValue());
        }

        return templateSector;
    }
}
