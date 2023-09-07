package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getAuthenticatedUser();

}
