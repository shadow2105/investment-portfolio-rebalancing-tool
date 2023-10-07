package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.domain.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    User getPrincipal() {
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
}
