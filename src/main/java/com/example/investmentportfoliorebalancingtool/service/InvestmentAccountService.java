package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface InvestmentAccountService {

    // add account returns boolean
    // - user uploads a file;
    // - file validity (is PDF) is checked by controller method
    // - takes user and valid file path as argument
    // - get the number of accounts already added to profile
    // - remove oldest account if >= 3 else simply add
    // - if account already there, remove and add newly to update
    // - user.getUserProfile().addInvestmentAccount(getAccount(fileName));
    // - returns added investment account object to be added to lrs cache for the session.
    // - each time the user adds a new account , lrs cache is updated [recently added account, selected template]

    InvestmentAccount addInvestmentAccount(User user, InvestmentAccount investmentAccount);

    Optional<InvestmentAccount> getInvestmentAccount(String fileName);

    Optional<InvestmentAccount> getInvestmentAccountFromId(User user, String id);

    Optional<InvestmentAccount> getInvestmentAccountFromAccountNumber(User user, String accountNumber);

    Set<InvestmentAccount> getInvestmentAccounts(User user);

    void removeInvestmentAccountById(User user, String id);

    void removeInvestmentAccountByAccountNumber(User user, String accountNumber);

    void removeInvestmentAccount(User user, InvestmentAccount investmentAccount);


    // For Individual Tools
    Map<AssetClass, BigDecimal> getAccountAssetsClassAllocation(InvestmentAccount investmentAccount);

    Map<CountryCodeAlpha2, BigDecimal> getAccountAssetsCountryAllocation(InvestmentAccount investmentAccount);

    Map<EquitySector, BigDecimal> getAccountEquitiesSectorAllocation(InvestmentAccount investmentAccount);


    // For Widgets

    // RECENTLY_ADDED_INVESTMENT_ACCOUNTS
    // {AccountNum1: [AccountHolder, TotalValue], AccountNum2: [AccountHolder, TotalValue], ... }
    Map<String, String[]> getRecentlyAddedInvestmentAccounts(User user);

    // ACCOUNTS_ASSET_CLASS_ALLOCATION
    // {AccountNum1: {CASH: %, EQUITY: %, ... }, AccountNum2: {CASH: %, EQUITY: %, ... }, ... }
    Map<String, Map<AssetClass, BigDecimal>> getAllAccountsAssetClassAllocation(User user);

    // ACCOUNTS_COUNTRY_ALLOCATION
    // {AccountNum1: {CA: %, US: %, ... }, AccountNum2: {CA: %, US: %, ... }, ... }
    Map<String, Map<CountryCodeAlpha2, BigDecimal>> getAllAccountsCountryAllocation(User user);

    // ACCOUNTS_SECTOR_ALLOCATION
    // {AccountNum1: {Energy: %, Financials: %, ... }, AccountNum2: {Energy: %, Financials: %, ... }, ... }
    Map<String, Map<EquitySector, BigDecimal>> getAllAccountsSectorAllocation(User user);


    // For Help
    int numberOfInvestmentAccounts(User user);

    BigDecimal calculateTotalAccountValue(InvestmentAccount investmentAccount);

    // (Account Equity %) * (Total Account Value)
    BigDecimal calculateAccountEquityValue(InvestmentAccount investmentAccount);
}
