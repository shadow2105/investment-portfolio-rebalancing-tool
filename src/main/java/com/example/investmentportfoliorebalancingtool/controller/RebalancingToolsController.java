package com.example.investmentportfoliorebalancingtool.controller;

import com.example.investmentportfoliorebalancingtool.domain.*;
import com.example.investmentportfoliorebalancingtool.service.InvestmentAccountService;
import com.example.investmentportfoliorebalancingtool.service.RebalanceConfigTemplateService;
import com.example.investmentportfoliorebalancingtool.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class RebalancingToolsController {

    private final UserService userService;
    private final InvestmentAccountService investmentAccountService;
    private final RebalanceConfigTemplateService rebalanceConfigTemplateService;

    public RebalancingToolsController(UserService userService, InvestmentAccountService investmentAccountService, RebalanceConfigTemplateService rebalanceConfigTemplateService) {
        this.userService = userService;
        this.investmentAccountService = investmentAccountService;
        this.rebalanceConfigTemplateService = rebalanceConfigTemplateService;
    }

    @GetMapping("/dashboard/rebalancing-tools/asset-class")
    public String rebalanceByAssetClass(@RequestParam("account_number") String accountNumber,
                                        @RequestParam("template_name") String templateName,
                                        Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);
            Optional<InvestmentAccount> investmentAccountOptional = investmentAccountService
                    .getInvestmentAccountFromAccountNumber(authenticatedUser, accountNumber);

            if (investmentAccountOptional.isPresent()) {
                InvestmentAccount selectedInvestmentAccount = investmentAccountOptional.get();

                model.addAttribute("investment_account", selectedInvestmentAccount);
                model.addAttribute("account_assets_class_allocation_map", investmentAccountService.
                        getAccountAssetsClassAllocation(selectedInvestmentAccount));
            }
            else {
                throw new RuntimeException("Unable to fetch Investment Account Details!");
            }

            Optional<RebalanceConfigTemplate> rebalanceConfigTemplateOptional = rebalanceConfigTemplateService
                    .getRebalancingTemplateFromName(authenticatedUser, templateName);


            if (rebalanceConfigTemplateOptional.isPresent()) {
                RebalanceConfigTemplate selectedRebalanceConfigTemplate = rebalanceConfigTemplateOptional.get();

                model.addAttribute("rebalancing_template", selectedRebalanceConfigTemplate);
                model.addAttribute("template_asset_class_allocation_map", rebalanceConfigTemplateService
                        .getTemplateAssetClassAllocation((RebalanceConfigTemplateAssetClass) selectedRebalanceConfigTemplate));
            }
            else {
                throw new RuntimeException("Unable to fetch Rebalancing Template Details!");
            }

        }
        else {
            throw new RuntimeException("Unable to Rebalance by Asset Class!");
        }

        return "rebalancing-tools/asset_class";
    }

    @GetMapping("/dashboard/rebalancing-tools/country")
    public String rebalanceByCountry(@RequestParam("account_number") String accountNumber,
                                        @RequestParam("template_name") String templateName,
                                        Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);
            Optional<InvestmentAccount> investmentAccountOptional = investmentAccountService
                    .getInvestmentAccountFromAccountNumber(authenticatedUser, accountNumber);

            if (investmentAccountOptional.isPresent()) {
                InvestmentAccount selectedInvestmentAccount = investmentAccountOptional.get();

                model.addAttribute("investment_account", selectedInvestmentAccount);
                model.addAttribute("account_assets_country_allocation_map", investmentAccountService.
                        getAccountAssetsCountryAllocation(selectedInvestmentAccount));
            }
            else {
                throw new RuntimeException("Unable to fetch Investment Account Details!");
            }

            Optional<RebalanceConfigTemplate> rebalanceConfigTemplateOptional = rebalanceConfigTemplateService
                    .getRebalancingTemplateFromName(authenticatedUser, templateName);


            if (rebalanceConfigTemplateOptional.isPresent()) {
                RebalanceConfigTemplate selectedRebalanceConfigTemplate = rebalanceConfigTemplateOptional.get();

                model.addAttribute("rebalancing_template", selectedRebalanceConfigTemplate);
                model.addAttribute("template_country_allocation_map", rebalanceConfigTemplateService
                        .getTemplateCountryAllocation((RebalanceConfigTemplateCountry) selectedRebalanceConfigTemplate));
            }
            else {
                throw new RuntimeException("Unable to fetch Rebalancing Template Details!");
            }

        }
        else {
            throw new RuntimeException("Unable to Rebalance by Country!");
        }

        return "rebalancing-tools/country";
    }

    @GetMapping("/dashboard/rebalancing-tools/equity-sector")
    public String rebalanceBySector(@RequestParam("account_number") String accountNumber,
                                        @RequestParam("template_name") String templateName,
                                        Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);
            Optional<InvestmentAccount> investmentAccountOptional = investmentAccountService
                    .getInvestmentAccountFromAccountNumber(authenticatedUser, accountNumber);

            if (investmentAccountOptional.isPresent()) {
                InvestmentAccount selectedInvestmentAccount = investmentAccountOptional.get();

                model.addAttribute("investment_account", selectedInvestmentAccount);
                model.addAttribute("account_equities_sector_allocation_map", investmentAccountService.
                        getAccountEquitiesSectorAllocation(selectedInvestmentAccount));
                model.addAttribute("account_equity_value", investmentAccountService.
                        calculateAccountEquityValue(selectedInvestmentAccount));
            }
            else {
                throw new RuntimeException("Unable to fetch Investment Account Details!");
            }

            Optional<RebalanceConfigTemplate> rebalanceConfigTemplateOptional = rebalanceConfigTemplateService
                    .getRebalancingTemplateFromName(authenticatedUser, templateName);


            if (rebalanceConfigTemplateOptional.isPresent()) {
                RebalanceConfigTemplate selectedRebalanceConfigTemplate = rebalanceConfigTemplateOptional.get();

                model.addAttribute("rebalancing_template", selectedRebalanceConfigTemplate);
                model.addAttribute("template_sector_allocation_map", rebalanceConfigTemplateService
                        .getTemplateSectorAllocation((RebalanceConfigTemplateSector) selectedRebalanceConfigTemplate));
            }
            else {
                throw new RuntimeException("Unable to fetch Rebalancing Template Details!");
            }

        }
        else {
            throw new RuntimeException("Unable to Rebalance by Equity Sector!");
        }

        return "rebalancing-tools/sector";
    }

    @GetMapping({"/dashboard/rebalancing-tools"})
    public String getAllRebalancingTools(Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);
            model.addAttribute("investment_accounts",
                    investmentAccountService.getInvestmentAccounts(authenticatedUser));

            model.addAttribute("rebalancing_templates_asset_class",
                    rebalanceConfigTemplateService.getAssetClassRebalancingTemplates(authenticatedUser));

            model.addAttribute("rebalancing_templates_country",
                    rebalanceConfigTemplateService.getCountryRebalancingTemplates(authenticatedUser));

            model.addAttribute("rebalancing_templates_sector",
                    rebalanceConfigTemplateService.getSectorRebalancingTemplates(authenticatedUser));

        }
        else {
            throw new RuntimeException("Unable to fetch Rebalancing Tools!");
        }

        return "rebalancing-tools/index";
    }
}
