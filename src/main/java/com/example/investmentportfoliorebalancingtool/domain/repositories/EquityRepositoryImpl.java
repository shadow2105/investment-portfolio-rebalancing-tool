package com.example.investmentportfoliorebalancingtool.domain.repositories;

import com.example.investmentportfoliorebalancingtool.domain.Equity;
import com.example.investmentportfoliorebalancingtool.domain.dao.EquityDAO;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EquityRepositoryImpl implements EquityRepository {

    private final EquityDAO equityDAO;

    public EquityRepositoryImpl(EquityDAO equityDAO) {
        this.equityDAO = equityDAO;
    }

    @Override
    public Optional<Equity> findBySymbol(String equitySymbol) {
        return Optional.ofNullable(equityDAO.read(equitySymbol));
    }
}
