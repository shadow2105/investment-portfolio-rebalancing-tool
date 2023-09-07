package com.example.investmentportfoliorebalancingtool.controller;

import com.example.investmentportfoliorebalancingtool.domain.InvestmentAccount;
import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.service.AssetService;
import com.example.investmentportfoliorebalancingtool.service.InvestmentAccountService;
import com.example.investmentportfoliorebalancingtool.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class AssetController {
    // get asset (GET)
    // add asset (POST)
    // update asset (PUT)
    // remove asset (DELETE)

    private final UserService userService;
    private final InvestmentAccountService investmentAccountService;
    private final AssetService assetService;

    public AssetController(UserService userService, InvestmentAccountService investmentAccountService, AssetService assetService) {
        this.userService = userService;
        this.investmentAccountService = investmentAccountService;
        this.assetService = assetService;
    }

    @GetMapping({"/dashboard/investment-accounts/{id}/assets/equity"})
    public String getAllEquityAssets(@PathVariable String id, Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            Optional<InvestmentAccount> investmentAccountOptional = investmentAccountService
                    .getInvestmentAccountFromId(authenticatedUser, id);

            model.addAttribute("user", authenticatedUser);

            if(investmentAccountOptional.isPresent()) {
                InvestmentAccount viewedInvestmentAccount = investmentAccountOptional.get();
                model.addAttribute("investment_account", viewedInvestmentAccount);
                model.addAttribute("account_equity_assets",
                        assetService.getAllEquityAssets(viewedInvestmentAccount));
            }
            else {
                throw new RuntimeException("Unable to fetch Investment Account Details!");
            }
        }
        else {
            throw new RuntimeException("Unable to fetch User Assets!");
        }

        return "investment-accounts/assets/equity";
    }
}
