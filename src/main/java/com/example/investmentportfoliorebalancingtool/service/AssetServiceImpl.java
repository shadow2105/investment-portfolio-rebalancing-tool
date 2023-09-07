package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.*;
import com.example.investmentportfoliorebalancingtool.domain.repositories.EquityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AssetServiceImpl implements AssetService {

    private final EquityRepository equityRepository;

    public AssetServiceImpl(EquityRepository equityRepository) {
        this.equityRepository = equityRepository;
    }

    @Override
    public Set<Asset> getAllCashAssets(InvestmentAccount investmentAccount) {
        // log
        Set<Asset> cashAssets = Collections.synchronizedSet(new LinkedHashSet<>());
        investmentAccount.getAssets().stream()
                .filter(asset -> asset.getAssetClass().equals(AssetClass.CASH))
                .iterator().forEachRemaining(cashAssets::add);

        return cashAssets;
    }

    @Override
    public Set<Asset> getAllEquityAssets(InvestmentAccount investmentAccount) {
        // log
        Set<Asset> equityAssets = Collections.synchronizedSet(new LinkedHashSet<>());
        investmentAccount.getAssets().stream()
                .filter(asset -> asset.getAssetClass().equals(AssetClass.EQUITY))
                .iterator().forEachRemaining(equityAssets::add);

        return equityAssets;
    }

    @Override
    public Set<Asset> getAllFixedIncomeAssets(InvestmentAccount investmentAccount) {
        // log
        Set<Asset> fixedIncomeAssets = Collections.synchronizedSet(new LinkedHashSet<>());
        investmentAccount.getAssets().stream()
                .filter(asset -> asset.getAssetClass().equals(AssetClass.FIXED_INCOME))
                .iterator().forEachRemaining(fixedIncomeAssets::add);

        return fixedIncomeAssets;
    }

    @Override
    public Set<Asset> getAllRealEstateAssets(InvestmentAccount investmentAccount) {
        // log
        Set<Asset> realEstateAssets = Collections.synchronizedSet(new LinkedHashSet<>());
        investmentAccount.getAssets().stream()
                .filter(asset -> asset.getAssetClass().equals(AssetClass.REAL_ESTATE))
                .iterator().forEachRemaining(realEstateAssets::add);

        return realEstateAssets;
    }

    @Override
    public Set<Asset> getAllCommodityAssets(InvestmentAccount investmentAccount) {
        // log
        Set<Asset> commodityAssets = Collections.synchronizedSet(new LinkedHashSet<>());
        investmentAccount.getAssets().stream()
                .filter(asset -> asset.getAssetClass().equals(AssetClass.COMMODITY))
                .iterator().forEachRemaining(commodityAssets::add);

        return commodityAssets;
    }

    @Override
    public Set<Asset> getAllUnknownAssets(InvestmentAccount investmentAccount) {
        // log
        Set<Asset> unknownAssets = Collections.synchronizedSet(new LinkedHashSet<>());
        investmentAccount.getAssets().stream()
                .filter(asset -> asset.getAssetClass().equals(AssetClass.UNKNOWN))
                .iterator().forEachRemaining(unknownAssets::add);

        return unknownAssets;
    }

    @Override
    @Transactional
    public String updateAccountsAssetsValuePerUnit(Set<InvestmentAccount> investmentAccounts) {
        StringBuffer importantNote = new StringBuffer();

        Set<String> nullAssetsName = new HashSet<>();
        for(InvestmentAccount investmentAccount: investmentAccounts) {
            for(Asset asset: investmentAccount.getAssets()) {
                if (asset.getAssetClass().equals(AssetClass.EQUITY)) {
                    // For equities, asset name = equity symbol
                    Optional<Equity> equityOptional = equityRepository.findBySymbol(asset.getName());
                    if (equityOptional.isPresent()) {
                        Equity equity = equityOptional.get();
                        asset.setValuePerUnit(equity.getValuePerUnit());
                    }
                    else {
                        nullAssetsName.add(asset.getName());
                    }
                }
            }
        }

        if(!nullAssetsName.isEmpty()) {
            importantNote.append("""
                    IMPORTANT:
                    
                    * Unable to update 'value per unit' for the following assets, and hence will use
                      their previous values in any related calculations. This may lead to unexpected 
                      results. To manually add the details of these assets use the form available when
                      adding an account. - 
                       
                    """);

            for(String assetName: nullAssetsName) {
                importantNote.append(assetName).append("  ");
            }
        }

        return importantNote.toString();
    }

    @Override
    @Transactional
    public String getAccountAssetsDetails(InvestmentAccount investmentAccount) {
        StringBuffer importantNote = new StringBuffer();

        StringBuffer nullAssets = new StringBuffer();
        for (Asset asset : investmentAccount.getAssets()) {
            if (asset.getAssetClass().equals(AssetClass.EQUITY) && asset instanceof Equity) {
                // For equities, asset name = equity symbol
                Optional<Equity> equityOptional = equityRepository.findBySymbol(asset.getName());
                if (equityOptional.isPresent()) {
                    Equity equity = equityOptional.get();
                    asset.setValuePerUnit(equity.getValuePerUnit());

                    String companyName = equity.getCompanyName();
                    String industry = equity.getIndustry();

                    if(companyName != null) {
                    ((Equity) asset).setCompanyName(companyName);
                    }

                    if(industry != null) {
                        ((Equity) asset).setIndustry(industry);
                    }

                    // can never be null. See EquityDAOUniBitAPI
                    ((Equity) asset).setSector(equity.getSector());
                }
                else {
                    asset.setValuePerUnit(BigDecimal.valueOf(0.0));
                    nullAssets.append(asset.getName()).append("  ");
                }
            }
        }

        if(!nullAssets.isEmpty()) {
            importantNote.append("""
                    IMPORTANT:
                    
                    * Unable to get details for the following assets, and hence will not be
                      included in any related calculations. This will lead to unexpected results.
                      To manually add the details of excluded assets for the added
                      investment account use the form below. -
                       
                    """).append(nullAssets);
        }

        return importantNote.toString();
    }


}