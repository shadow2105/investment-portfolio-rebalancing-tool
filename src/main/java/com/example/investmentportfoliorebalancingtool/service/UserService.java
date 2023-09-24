package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.User;

import java.util.Optional;
import java.util.Set;

public interface UserService {
    Optional<User> getAuthenticatedUser();

    Set<User> getRegisteredUsers();

    void removeRegisteredUserById(String id);
}
