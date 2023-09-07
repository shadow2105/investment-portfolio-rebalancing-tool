package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false, nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userProfile", orphanRemoval = true)
    private Set<InvestmentAccount> investmentAccounts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userProfile", orphanRemoval = true)
    private Set<RebalanceConfigTemplate> rebalanceConfigTemplates;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userProfile")
    private Set<Widget> widgets;

    public UserProfile() {
    }

    public UserProfile(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<InvestmentAccount> getInvestmentAccounts() {
        return investmentAccounts;
    }

    public void setInvestmentAccounts(Set<InvestmentAccount> investmentAccounts) {
        this.investmentAccounts = investmentAccounts;
    }

    public InvestmentAccount addInvestmentAccount(InvestmentAccount investmentAccount) {
        investmentAccount.setUserProfile(this);
        this.investmentAccounts.add(investmentAccount);

        return investmentAccount;
    }

    public boolean removeInvestmentAccount(InvestmentAccount investmentAccount) {
        try {
            this.investmentAccounts.remove(investmentAccount);
            investmentAccount.setUserProfile(null);
            return true;
        }
        catch(Exception e) {
            // add log
            System.out.println(e);
        }

        return false;
    }

    public Set<RebalanceConfigTemplate> getRebalanceConfigTemplates() {
        return rebalanceConfigTemplates;
    }

    public void setRebalanceConfigTemplates(Set<RebalanceConfigTemplate> rebalanceConfigTemplates) {
        this.rebalanceConfigTemplates = rebalanceConfigTemplates;
    }

    public RebalanceConfigTemplate addRebalanceConfigTemplate(RebalanceConfigTemplate rebalanceConfigTemplate) {
        rebalanceConfigTemplate.setUserProfile(this);
        this.rebalanceConfigTemplates.add(rebalanceConfigTemplate);

        return rebalanceConfigTemplate;
    }

    public boolean removeRebalanceConfigTemplate(RebalanceConfigTemplate rebalanceConfigTemplate) {
        try {
            this.rebalanceConfigTemplates.remove(rebalanceConfigTemplate);
            rebalanceConfigTemplate.setUserProfile(null);
            return true;
        }
        catch(Exception e) {
            // add log
            System.out.println(e);
        }

        return false;
    }

    public Set<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(Set<Widget> widgets) {
        this.widgets = widgets;
    }
}
