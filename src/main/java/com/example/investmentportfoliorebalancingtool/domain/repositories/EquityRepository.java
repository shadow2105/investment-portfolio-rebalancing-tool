package com.example.investmentportfoliorebalancingtool.domain.repositories;

import com.example.investmentportfoliorebalancingtool.domain.Equity;

import java.util.Optional;

public interface EquityRepository {

    Optional<Equity> findBySymbol(String equitySymbol);
}
