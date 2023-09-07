package com.example.investmentportfoliorebalancingtool.domain;

import jakarta.persistence.Column;

import java.io.Serializable;
import java.util.UUID;

public class AssetId implements Serializable {
    private String name;
    private UUID investmentAccount;

    public AssetId() {
    }

    public String getName() {
        return name;
    }

    public UUID getInvestmentAccount() {
        return investmentAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetId assetID = (AssetId) o;

        if (!getName().equals(assetID.getName())) return false;
        return getInvestmentAccount().equals(assetID.getInvestmentAccount());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getInvestmentAccount().hashCode();
        return result;
    }
}
