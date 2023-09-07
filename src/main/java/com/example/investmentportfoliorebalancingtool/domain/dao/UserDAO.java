package com.example.investmentportfoliorebalancingtool.domain.dao;

import com.example.investmentportfoliorebalancingtool.domain.User;

import java.util.Set;
import java.util.UUID;

public interface UserDAO {
    void create(User user);
    User read(UUID id);
    User read(String userName);
    Set<User> readAll();

    User update(User user);
    void delete(User user);
}
