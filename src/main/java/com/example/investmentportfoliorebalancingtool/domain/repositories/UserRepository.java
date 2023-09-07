package com.example.investmentportfoliorebalancingtool.domain.repositories;

import com.example.investmentportfoliorebalancingtool.domain.User;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);
    Optional<User> findByUserName(String userName);

    Set<User> findAll();

    void remove(User user);

}
