package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "user_investment_accounts")
public class InvestmentAccount extends BaseEntityAudit {

    // account number is unique;
    // another user trying to add account with same account number will result in -
    // org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException: Unique index or primary key violation:

    // For same user trying to add account with same account number will lead to an update
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_holder_first_name", nullable = false)
    private String accountHolderFirstName;

    @Column(name = "account_holder_last_name", nullable = false)
    private String accountHolderLastName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "investmentAccount")
    private List<Asset> assets;

    //@Transient
    private BigDecimal accountValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    private UserProfile userProfile;

    public InvestmentAccount() {
    }

    public InvestmentAccount(String accountNumber, String accountHolderFirstName, String accountHolderLastName, List<Asset> assets) {
        this.accountNumber = accountNumber;
        this.accountHolderFirstName = accountHolderFirstName;
        this.accountHolderLastName = accountHolderLastName;
        this.assets = assets;
        this.accountValue = BigDecimal.valueOf(0.0);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderFirstName() {
        return accountHolderFirstName;
    }

    public void setAccountHolderFirstName(String accountHolderFirstName) {
        this.accountHolderFirstName = accountHolderFirstName;
    }

    public String getAccountHolderLastName() {
        return accountHolderLastName;
    }

    public void setAccountHolderLastName(String accountHolderLastName) {
        this.accountHolderLastName = accountHolderLastName;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public InvestmentAccount addAsset(Asset asset) {
        asset.setInvestmentAccount(this);
        this.assets.add(asset);
        return this;
    }

    public BigDecimal getAccountValue() {
        return accountValue;
    }


    public void setAccountValue(BigDecimal accountValue) {
        this.accountValue = accountValue;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
