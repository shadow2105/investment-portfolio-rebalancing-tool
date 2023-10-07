package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserServiceImpl(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    @Transactional
    public Optional<User> getAuthenticatedUser() {
        User principal = authService.getPrincipal();
        if(principal != null) {
            Optional<User> userOptional = userRepository.findByUserName(principal.getUserName());

            /* Except UserName and email, all user fields can be updated using Okta
             * To reflect the changes in local database without deleting the updated user's entry
             * set the user's attributes as that of principal. If there are any changes, Hibernate will
             * automatically generate update statements.
             */
            User user;
            if(userOptional.isPresent()) {
                user = userOptional.get();
                user.setRole(principal.getRole());  // Check if user role is updated
                user.setUpdatedBy(principal.getUserName());
            }
            else {
                user = principal;
                user.setCreatedBy(principal.getUserName());
            }
            return Optional.ofNullable(userRepository.save(user));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Set<User> getRegisteredUsers() {
        // log
        Set<User> registeredUsers = Collections.synchronizedSet(new LinkedHashSet<>());
        userRepository.findAll().iterator().forEachRemaining(registeredUsers::add);
        return registeredUsers;
    }

    @Override
    @Transactional
    public void removeRegisteredUserById(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        userOptional.ifPresent(userRepository::remove);
    }
}
