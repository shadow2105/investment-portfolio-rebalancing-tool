package com.example.investmentportfoliorebalancingtool.controller;

import com.example.investmentportfoliorebalancingtool.cache.LRUCache;
import com.example.investmentportfoliorebalancingtool.domain.InvestmentAccount;
import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.domain.UserProfile;
import com.example.investmentportfoliorebalancingtool.domain.WidgetDescription;
import com.example.investmentportfoliorebalancingtool.service.AssetService;
import com.example.investmentportfoliorebalancingtool.service.InvestmentAccountService;
import com.example.investmentportfoliorebalancingtool.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final UserService userService;
    private final InvestmentAccountService investmentAccountService;
    private final AssetService assetService;
    protected User authenticatedUser;
    protected LRUCache<WidgetDescription, Object> dashboardWidgetCache;

    private final EntityManager em;

    public CustomLoginSuccessHandler(UserService userService, InvestmentAccountService investmentAccountService, AssetService assetService, EntityManager em) {
        this.userService = userService;
        this.investmentAccountService = investmentAccountService;
        this.assetService = assetService;
        this.em = em;
    }

    /*
        - User authenticates
        - get authenticated user; save if new user or merge if existing user
        - create lru cache instance to hold widget objects
        - updateAccountsAssetsValuePerUnit (assetService)
        - execute widget (widgets mapped/added to user profile) methods to get widget objects
        - add widget objects to cache
        - cache is only updated if (new account is added, existing account is modified, template is added or modified)
         */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            authenticatedUser = userOptional.get();

            System.out.println(em.contains(authenticatedUser)); // false
            System.out.println("\n<<<<< Logged in Successfully; Performing Background tasks >>>>>\n");
            dashboardWidgetCache = new LRUCache<>(5);

            // org.hibernate.LazyInitializationException:
            // failed to lazily initialize a collection of role: com.example.investmentportfoliorebalancingtool.domain.UserProfile.investmentAccounts:
            // could not initialize proxy - no Session

            //assetService.updateAccountsAssetsValuePerUnit(investmentAccountService.getInvestmentAccounts(authenticatedUser));
        }
        else {
            throw new RuntimeException("Unable to authenticate!");
        }
    }
}
