package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.Asset;
import com.example.investmentportfoliorebalancingtool.domain.AssetClass;
import com.example.investmentportfoliorebalancingtool.domain.InvestmentAccount;
import com.example.investmentportfoliorebalancingtool.domain.User;

import java.util.Set;

public interface AssetService {

    // manually update asset in added account
    // Called everytime user logs in
    // Returns a warning note when unable to update values for certain assets (equity symbols)
    // "using previously saved values"
    String updateAccountsAssetsValuePerUnit(Set<InvestmentAccount> investmentAccounts);

    // Called when the user adds a new account
    // Returns a warning note when unable to update values for certain assets
    // - Initialize values for those assets to zero, so are not added in calculations
    // - "asks user to manually add update values for added assets (equity symbols)
    String getAccountAssetsDetails(InvestmentAccount investmentAccount);

    Set<Asset> getAllCashAssets(InvestmentAccount investmentAccount);
    Set<Asset> getAllEquityAssets(InvestmentAccount investmentAccount);
    Set<Asset> getAllFixedIncomeAssets(InvestmentAccount investmentAccount);
    Set<Asset> getAllRealEstateAssets(InvestmentAccount investmentAccount);
    Set<Asset> getAllCommodityAssets(InvestmentAccount investmentAccount);
    Set<Asset> getAllUnknownAssets(InvestmentAccount investmentAccount);
}
