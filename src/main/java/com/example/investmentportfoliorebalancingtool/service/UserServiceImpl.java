package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.domain.UserRole;
import com.example.investmentportfoliorebalancingtool.domain.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User getPrincipal() {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            OidcUser principalUser = (OidcUser) authentication.getPrincipal();

            UserRole role = UserRole.USER;
            if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("admin"))) {
                role = UserRole.ADMIN;
            }

            return new User(principalUser.getGivenName(),
                    principalUser.getMiddleName(),
                    principalUser.getFamilyName(),
                    principalUser.getPreferredUsername(),
                    principalUser.getEmail(),
                    principalUser.getPicture(),
                    principalUser.getLocale(),
                    role);
        }
        catch (Exception e) {
            System.out.println("Unable to fetch Authenticated User details!");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    @Transactional
    public Optional<User> getAuthenticatedUser() {
        User principal = getPrincipal();
        if(principal != null) {
            Optional<User> userOptional = userRepository.findByUserName(principal.getUserName());

            User user;
            if(userOptional.isPresent()) {
                user = userOptional.get();
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
}
