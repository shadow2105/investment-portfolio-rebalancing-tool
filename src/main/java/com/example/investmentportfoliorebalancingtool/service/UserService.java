package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.User;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    Optional<User> getAuthenticatedUser();

    Set<User> getRegisteredUsers();

    void removeRegisteredUserById(UUID id);
}
