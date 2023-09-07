package com.example.investmentportfoliorebalancingtool.domain.dao;

import com.example.investmentportfoliorebalancingtool.domain.Equity;

public interface EquityDAO {

    Equity read(String equitySymbol);
}
