package com.example.investmentportfoliorebalancingtool.domain;

import java.io.Serializable;
import java.util.UUID;

public class RebalanceConfigTemplateId implements Serializable {
    private String name;
    private UUID userProfile;

    public RebalanceConfigTemplateId() {
    }

    public String getName() {
        return name;
    }

    public UUID getUserProfile() {
        return userProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RebalanceConfigTemplateId that = (RebalanceConfigTemplateId) o;

        if (!getName().equals(that.getName())) return false;
        return getUserProfile().equals(that.getUserProfile());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getUserProfile().hashCode();
        return result;
    }
}
