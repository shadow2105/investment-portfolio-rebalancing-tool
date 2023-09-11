package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class InvestmentAccountServiceImpl implements InvestmentAccountService {

    @Override
    @Transactional
    public InvestmentAccount addInvestmentAccount(User user, InvestmentAccount investmentAccount) {
        investmentAccount.setCreatedBy(user.getUserName());
        investmentAccount.setUpdatedBy(user.getUserName());
        return user.getUserProfile().addInvestmentAccount(investmentAccount);
    }

    @Override
    public Optional<InvestmentAccount> getInvestmentAccount(String fileName) {
        // Handle NumberFormatException (BigDecimal.ValueOf)
        try {
            Map<String, Object> accountData = (Map<String, Object>) getAccountData(fileName).get("result_data");

            InvestmentAccount investmentAccount = new InvestmentAccount();
            investmentAccount.setAssets(new ArrayList<>());

            for(Map<String, String> asset: (ArrayList<Map<String, String>>) accountData.get("assets")) {
                String assetName = asset.get("asset");
                AssetClass assetClass = AssetClass.valueOf(asset.get("asset_class"));
                if(assetClass.equals(AssetClass.EQUITY)) {
                    //For equities, asset name = equity symbol
                    investmentAccount.addAsset(new Equity(assetName,
                            BigDecimal.valueOf(Double.parseDouble(asset.get("number_of_units"))),
                            BigDecimal.valueOf(Double.parseDouble(asset.get("value_per_unit"))),
                            CountryCodeAlpha2.valueOf(asset.get("country")),
                            assetName,
                            "Not Available",
                            EquitySector.UNKNOWN,
                            "Not Available"));
                }

                // if(assetClass.equals(AssetClass.CASH))
                // if(assetClass.equals(AssetClass.FIXED_INCOME))
                // if(assetClass.equals(AssetClass.REAL_ESTATE))
                // if(assetClass.equals(AssetClass.COMMODITY))
            }

            investmentAccount.setAccountNumber((String) accountData.get("account_num"));
            investmentAccount.setAccountHolderFirstName((String) accountData.get("first_name"));
            investmentAccount.setAccountHolderLastName((String) accountData.get("last_name"));

            return Optional.of(investmentAccount);
        }
        catch (Exception e) {
            // add log
            System.out.println("Unable to fetch Investment Account details!");
            System.out.println(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<InvestmentAccount> getInvestmentAccountFromId(User user, String id) {
        return user.getUserProfile()
                .getInvestmentAccounts().stream()
                .filter(account -> account.getId().equals(UUID.fromString(id)))
                .findFirst();
    }

    @Override
    public Optional<InvestmentAccount> getInvestmentAccountFromAccountNumber(User user, String accountNumber) {
        return user.getUserProfile()
                .getInvestmentAccounts().stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    @Override
    @Transactional
    public Set<InvestmentAccount> getInvestmentAccounts(User user) {
        // log
        Set<InvestmentAccount> investmentAccounts = Collections.synchronizedSet(new LinkedHashSet<>());
        user.getUserProfile().getInvestmentAccounts().iterator().forEachRemaining(investmentAccounts::add);
        return investmentAccounts;
    }

    @Override
    @Transactional
    public void removeInvestmentAccountById(User user, String id) {
        for(InvestmentAccount investmentAccount: user.getUserProfile().getInvestmentAccounts()) {
            if (investmentAccount.getId().equals(UUID.fromString(id))) {
                user.getUserProfile().removeInvestmentAccount(investmentAccount);
                return;
            }
        }
    }

    @Override
    @Transactional
    public void removeInvestmentAccountByAccountNumber(User user, String accountNumber) {
        for(InvestmentAccount investmentAccount: user.getUserProfile().getInvestmentAccounts()) {
            if (investmentAccount.getAccountNumber().equals(accountNumber)) {
                user.getUserProfile().removeInvestmentAccount(investmentAccount);
                return;
            }
        }
    }

    @Override
    @Transactional
    public void removeInvestmentAccount(User user, InvestmentAccount investmentAccount) {
        user.getUserProfile().removeInvestmentAccount(investmentAccount);
    }

    @Override
    @Transactional
    public Map<AssetClass, BigDecimal> getAccountAssetsClassAllocation(InvestmentAccount investmentAccount) {
        Map<AssetClass, BigDecimal> allocationMap = Collections.synchronizedMap(new LinkedHashMap<>());
        for(AssetClass assetClass: AssetClass.values()) {
            allocationMap.put(assetClass, BigDecimal.valueOf(0.0));
        }

        BigDecimal totalAccountValue = BigDecimal.valueOf(0.0);
        // Assuming each asset is stored in with a value in the database, when an account is added.
        // Will run related AssetService method each time user adds an account or logs in to update asset value details.
        for(Asset asset: investmentAccount.getAssets()) {
            BigDecimal assetValue = asset.getNumOfUnits().multiply(asset.getValuePerUnit());
            totalAccountValue = totalAccountValue.add(assetValue);

            switch(asset.getAssetClass()) {
                case CASH -> allocationMap.put(AssetClass.CASH, allocationMap.get(AssetClass.CASH).add(assetValue));
                case EQUITY -> allocationMap.put(AssetClass.EQUITY, allocationMap.get(AssetClass.EQUITY).add(assetValue));
                case FIXED_INCOME -> allocationMap.put(AssetClass.FIXED_INCOME, allocationMap.get(AssetClass.FIXED_INCOME).add(assetValue));
                case REAL_ESTATE -> allocationMap.put(AssetClass.REAL_ESTATE, allocationMap.get(AssetClass.REAL_ESTATE).add(assetValue));
                case COMMODITY -> allocationMap.put(AssetClass.COMMODITY, allocationMap.get(AssetClass.COMMODITY).add(assetValue));
                case UNKNOWN -> allocationMap.put(AssetClass.UNKNOWN, allocationMap.get(AssetClass.UNKNOWN).add(assetValue));
            }
        }

        BigDecimal finalTotalAccountValue = totalAccountValue;
        allocationMap.replaceAll((k, v) -> v.multiply(BigDecimal.valueOf(100)).divide(finalTotalAccountValue, 2, RoundingMode.HALF_UP));

        return allocationMap;
    }

    @Override
    @Transactional
    public Map<CountryCodeAlpha2, BigDecimal> getAccountAssetsCountryAllocation(InvestmentAccount investmentAccount) {
        Map<CountryCodeAlpha2, BigDecimal> allocationMap = Collections.synchronizedMap(new LinkedHashMap<>());
        for(CountryCodeAlpha2 country: CountryCodeAlpha2.values()) {
            allocationMap.put(country, BigDecimal.valueOf(0.0));
        }

        BigDecimal totalAccountValue = BigDecimal.valueOf(0.0);
        // Assuming each asset is stored in with a value in the database, when an account is added.
        // Will run related AssetService method each time user adds an account or logs in to update asset value details.
        for(Asset asset: investmentAccount.getAssets()) {
            BigDecimal assetValue = asset.getNumOfUnits().multiply(asset.getValuePerUnit());
            totalAccountValue = totalAccountValue.add(assetValue);

            switch(asset.getCountry()) {
                case CA -> allocationMap.put(CountryCodeAlpha2.CA, allocationMap.get(CountryCodeAlpha2.CA).add(assetValue));
                case US -> allocationMap.put(CountryCodeAlpha2.US, allocationMap.get(CountryCodeAlpha2.US).add(assetValue));
                case UNKNOWN -> allocationMap.put(CountryCodeAlpha2.UNKNOWN, allocationMap.get(CountryCodeAlpha2.UNKNOWN).add(assetValue));
            }
        }

        BigDecimal finalTotalAccountValue = totalAccountValue;
        allocationMap.replaceAll((k, v) -> v.multiply(BigDecimal.valueOf(100)).divide(finalTotalAccountValue, 2, RoundingMode.HALF_UP));

        return allocationMap;
    }

    @Override
    @Transactional
    public Map<EquitySector, BigDecimal> getAccountEquitiesSectorAllocation(InvestmentAccount investmentAccount) {
        Map<EquitySector, BigDecimal> allocationMap = Collections.synchronizedMap(new LinkedHashMap<>());
        for(EquitySector equitySector: EquitySector.values()) {
            allocationMap.put(equitySector, BigDecimal.valueOf(0.0));
        }

        BigDecimal totalEquityValue = BigDecimal.valueOf(0.0);
        // Assuming each asset is stored in with a value in the database, when an account is added.
        // Will run related AssetService method each time user adds an account or logs in to update asset value details.
        for(Asset asset: investmentAccount.getAssets()) {
            if(asset.getAssetClass().equals(AssetClass.EQUITY) && asset instanceof Equity) {
                BigDecimal assetValue = asset.getNumOfUnits().multiply(asset.getValuePerUnit());
                totalEquityValue = totalEquityValue.add(assetValue);

                switch(((Equity) asset).getSector()) {
                    case ENERGY -> allocationMap.put(EquitySector.ENERGY, allocationMap.get(EquitySector.ENERGY).add(assetValue));

                    case MATERIALS -> allocationMap.put(EquitySector.MATERIALS, allocationMap.get(EquitySector.MATERIALS).add(assetValue));

                    case INDUSTRIALS -> allocationMap.put(EquitySector.INDUSTRIALS, allocationMap.get(EquitySector.INDUSTRIALS).add(assetValue));

                    case UTILITIES -> allocationMap.put(EquitySector.UTILITIES, allocationMap.get(EquitySector.UTILITIES).add(assetValue));

                    case HEALTHCARE -> allocationMap.put(EquitySector.HEALTHCARE, allocationMap.get(EquitySector.HEALTHCARE).add(assetValue));

                    case FINANCIALS -> allocationMap.put(EquitySector.FINANCIALS, allocationMap.get(EquitySector.FINANCIALS).add(assetValue));

                    case CONSUMER_DISCRETIONARY -> allocationMap.put(EquitySector.CONSUMER_DISCRETIONARY, allocationMap.get(EquitySector.CONSUMER_DISCRETIONARY).add(assetValue));

                    case CONSUMER_STAPLES -> allocationMap.put(EquitySector.CONSUMER_STAPLES, allocationMap.get(EquitySector.CONSUMER_STAPLES).add(assetValue));

                    case INFORMATION_TECHNOLOGY -> allocationMap.put(EquitySector.INFORMATION_TECHNOLOGY, allocationMap.get(EquitySector.INFORMATION_TECHNOLOGY).add(assetValue));

                    case COMMUNICATION_SERVICES -> allocationMap.put(EquitySector.COMMUNICATION_SERVICES, allocationMap.get(EquitySector.COMMUNICATION_SERVICES).add(assetValue));

                    case REAL_ESTATE -> allocationMap.put(EquitySector.REAL_ESTATE, allocationMap.get(EquitySector.REAL_ESTATE).add(assetValue));

                    case UNKNOWN -> allocationMap.put(EquitySector.UNKNOWN, allocationMap.get(EquitySector.UNKNOWN).add(assetValue));
                }
            }
        }

        BigDecimal finalTotalEquityValue = totalEquityValue;
        allocationMap.replaceAll((k, v) -> v.multiply(BigDecimal.valueOf(100)).divide(finalTotalEquityValue, 2, RoundingMode.HALF_UP));

        return allocationMap;
    }

    @Override
    @Transactional
    public Map<String, String[]> getRecentlyAddedInvestmentAccounts(User user) {
        Map<String, String[]> userInvestmentAccounts = Collections.synchronizedMap(new LinkedHashMap<>());

        for(InvestmentAccount investmentAccount: user.getUserProfile().getInvestmentAccounts()) {
            userInvestmentAccounts.put(
                    investmentAccount.getAccountNumber(),
                    new String[]{investmentAccount.getAccountHolderFirstName() + " " +
                                 investmentAccount.getAccountHolderLastName(),
                                 investmentAccount.getAccountValue().toString()}
            );
        }

        return userInvestmentAccounts;
    }

    @Override
    @Transactional
    public Map<String, Map<AssetClass, BigDecimal>> getAllAccountsAssetClassAllocation(User user) {
        Map<String, Map<AssetClass, BigDecimal>> userInvestmentAccountsAssetClassAllocation = Collections.synchronizedMap(new LinkedHashMap<>());

        for(InvestmentAccount investmentAccount: user.getUserProfile().getInvestmentAccounts()) {
            userInvestmentAccountsAssetClassAllocation.put(
                    investmentAccount.getAccountNumber(),
                    getAccountAssetsClassAllocation(investmentAccount)
            );
        }

        return userInvestmentAccountsAssetClassAllocation;
    }

    @Override
    @Transactional
    public Map<String, Map<CountryCodeAlpha2, BigDecimal>> getAllAccountsCountryAllocation(User user) {
        Map<String, Map<CountryCodeAlpha2, BigDecimal>> userInvestmentAccountsCountryAllocation = Collections.synchronizedMap(new LinkedHashMap<>());

        for(InvestmentAccount investmentAccount: user.getUserProfile().getInvestmentAccounts()) {
            userInvestmentAccountsCountryAllocation.put(
                    investmentAccount.getAccountNumber(),
                    getAccountAssetsCountryAllocation(investmentAccount)
            );
        }

        return userInvestmentAccountsCountryAllocation;
    }

    @Override
    @Transactional
    public Map<String, Map<EquitySector, BigDecimal>> getAllAccountsSectorAllocation(User user) {
        Map<String, Map<EquitySector, BigDecimal>> userInvestmentAccountsSectorAllocation = Collections.synchronizedMap(new LinkedHashMap<>());

        for(InvestmentAccount investmentAccount: user.getUserProfile().getInvestmentAccounts()) {
            userInvestmentAccountsSectorAllocation.put(
                    investmentAccount.getAccountNumber(),
                    getAccountEquitiesSectorAllocation(investmentAccount)
            );
        }

        return userInvestmentAccountsSectorAllocation;
    }

    @Override
    public int numberOfInvestmentAccounts(User user) {
        return user.getUserProfile().getInvestmentAccounts().size();
    }


    /* Observed difference in calculation of 'total account value' and 'account equity value' when equity is 100% of account
     *
     * Getting calculated before the data (num_of_units, value_per_unit) is being persisted to the database
     * num_of_units had a scale of 2 in database column (now changed to 4; see class 'Asset')
     * value_per_unit has a scale of 3 in the database column
     */
    @Override
    public BigDecimal calculateTotalAccountValue(InvestmentAccount investmentAccount) {
        BigDecimal totalAccountValue = BigDecimal.valueOf(0.0);
        // Assuming each asset is stored in with a value in the database, when an account is added.
        // Will run related AssetService method each time user adds an account or logs in to update asset value details.
        for(Asset asset: investmentAccount.getAssets()) {
            //System.out.println(totalAccountValue + "     " + asset.getNumOfUnits() + "     " + asset.getValuePerUnit());
            totalAccountValue = totalAccountValue.add(asset.getNumOfUnits().multiply(asset.getValuePerUnit()));
        }

        return totalAccountValue.setScale(2, RoundingMode.HALF_UP);
    }


    /* Observed difference in calculation of 'total account value' and 'account equity value' when equity is 100% of account
     *
     * Getting calculated after the data (num_of_units, value_per_unit) is persisted to the database
     * num_of_units had a scale of 2 in the investment account statement (pdf)
     * value_per_unit has a scale of 3 in the data being fetched from (UniBit API)
     */
    @Override
    public BigDecimal calculateAccountEquityValue(InvestmentAccount investmentAccount) {
        BigDecimal accountEquityValue = BigDecimal.valueOf(0.0);

        for(Asset asset: investmentAccount.getAssets()) {
            if (asset.getAssetClass().equals(AssetClass.EQUITY) && asset instanceof Equity) {
                //System.out.println(accountEquityValue + "     " + asset.getNumOfUnits() + "     " + asset.getValuePerUnit());
                accountEquityValue = accountEquityValue.add(asset.getNumOfUnits().multiply(asset.getValuePerUnit()));

            }
        }

        return accountEquityValue.setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> getAccountData(String filePath) throws IOException {
        // Build the Command Line String
        String commandStr = "python \"" +
                new File("src/main/resources/scripts/fetch_account_data.py").getAbsolutePath() +
                "\" --filepath " +
                filePath;

        // Create a CommandLine object from parsing the Command Line String
        CommandLine cmdLine = CommandLine.parse(commandStr);

        // Create ByteArrayOutputStream object to write the output of the sub-process (Python Script)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Create PumpStreamHandler object to -
        // - copy standard output and error of sub-processes to standard output and error of the parent process.
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        // Create DefaultExecutor object to be able to start the subprocess, and
        // set Stream Handler to capture the subprocess output of stdout and stderr
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);

        // Execute the subprocess and retrieve exit code
        int exitValue = executor.execute(cmdLine);

        // Create Jackson Databind ObjectMapper object
        ObjectMapper objectMapper = new ObjectMapper();

        // Create TypeReference object
        return objectMapper.readValue(outputStream.toString(), new TypeReference<Map<String, Object>>() {
        });
    }
}
