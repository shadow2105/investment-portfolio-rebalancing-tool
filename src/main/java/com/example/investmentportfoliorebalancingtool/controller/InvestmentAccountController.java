package com.example.investmentportfoliorebalancingtool.controller;

import com.example.investmentportfoliorebalancingtool.domain.InvestmentAccount;
import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.service.AssetService;
import com.example.investmentportfoliorebalancingtool.service.InvestmentAccountService;
import com.example.investmentportfoliorebalancingtool.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

@Controller
public class InvestmentAccountController {

    private final UserService userService;
    private final InvestmentAccountService investmentAccountService;
    private final AssetService assetService;

    public InvestmentAccountController(UserService userService, InvestmentAccountService investmentAccountService, AssetService assetService) {
        this.userService = userService;
        this.investmentAccountService = investmentAccountService;
        this.assetService = assetService;
    }

    @PostMapping("/dashboard/investment-accounts")
    public String saveInvestmentAccount(@RequestParam("file") MultipartFile file) {
        Optional<User> userOptional = userService.getAuthenticatedUser();

        if(userOptional.isPresent() &&
                FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("pdf")) {

            User authenticatedUser = userOptional.get();

            try {
                // Give a random filename here.
                String filename = authenticatedUser.getId().toString() + ".pdf";
                byte[] bytes = file.getBytes();

                // Directory path where you want to save ;
                Path tempPath = Paths.get(System.getProperty("java.io.tmpdir") + filename);
                Files.write(tempPath, bytes);

                Optional<InvestmentAccount> investmentAccountOptional = investmentAccountService.
                        getInvestmentAccount(tempPath.toString());

                Files.delete(tempPath);

                if (investmentAccountOptional.isPresent()) {
                    InvestmentAccount investmentAccount = investmentAccountOptional.get();

                    // if account already exists, remove its previous instance, else do nothing
                    investmentAccountService.removeInvestmentAccountByAccountNumber(authenticatedUser,
                            investmentAccount.getAccountNumber());

                    // Check number of existing user accounts (max 3 can be stored)
                    if (investmentAccountService.numberOfInvestmentAccounts(authenticatedUser) >= 3) {
                        // first, find and remove the oldest account
                        InvestmentAccount oldestInvestmentAccount = authenticatedUser.
                                getUserProfile().
                                getInvestmentAccounts().stream().
                                max(Comparator.comparing(InvestmentAccount::getCreatedAt)).
                                get();

                        // System.out.println(oldestUserAccount.getAccountNumber());
                        investmentAccountService.removeInvestmentAccount(authenticatedUser,
                                oldestInvestmentAccount);

                    }
                    // get current asset values
                    // calculate total account value
                    // and then add the new account

                    assetService.getAccountAssetsDetails(investmentAccount);
                    investmentAccount.setAccountValue(
                            investmentAccountService.calculateTotalAccountValue(investmentAccount));

                    investmentAccountService.addInvestmentAccount(authenticatedUser, investmentAccount);


                } else {
                    throw new RuntimeException("Unable to Read Investment Account Statement!");
                }
            }
            catch (IOException e) {
                System.out.println("Unable to process uploaded file!");
            }
            catch (Exception e) {
                if(e instanceof JdbcSQLIntegrityConstraintViolationException ||
                        e instanceof org.springframework.dao.DataIntegrityViolationException) {
                    System.out.println("Unable to add Investment Account!\nThis account is mapped to another user.");
                }
                else {
                    System.out.println(e);
                }
            }
        }
        else {
            throw new RuntimeException("Unable to process uploaded file!");
        }

        return "redirect:/dashboard/investment-accounts";
    }

    @GetMapping({"/dashboard/investment-accounts/{id}"})
    public String getInvestmentAccount(@PathVariable String id, Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            Optional<InvestmentAccount> investmentAccountOptional = investmentAccountService
                    .getInvestmentAccountFromId(authenticatedUser, id);

            model.addAttribute("user", authenticatedUser);

            if(investmentAccountOptional.isPresent()) {
                InvestmentAccount viewedInvestmentAccount = investmentAccountOptional.get();
                model.addAttribute("investment_account", viewedInvestmentAccount);
                model.addAttribute("account_assets_class_allocation_map", investmentAccountService.
                        getAccountAssetsClassAllocation(viewedInvestmentAccount));
            }
            else {
                throw new RuntimeException("Unable to fetch Investment Account Details!");
            }
        }
        else {
            throw new RuntimeException("Unable to fetch User Investment Accounts!");
        }

        return "investment-accounts/view";
    }

    @GetMapping({"/dashboard/investment-accounts"})
    public String getAllInvestmentAccounts(Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);
            model.addAttribute("investment_accounts",
                    investmentAccountService.getInvestmentAccounts(authenticatedUser));
        }
        else {
            throw new RuntimeException("Unable to fetch User Investment Accounts!");
        }

        return "investment-accounts/index";
    }

    // HTML doesn't support HTTP DELETE
    @GetMapping({"/dashboard/investment-accounts/{id}/delete"})
    public String deleteInvestmentAccount(@PathVariable String id) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if (userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            investmentAccountService.removeInvestmentAccountById(authenticatedUser, id);
        }
        else {
            throw new RuntimeException("Unable to fetch User Investment Accounts!");
        }

        return "redirect:/dashboard/investment-accounts";
    }
}
