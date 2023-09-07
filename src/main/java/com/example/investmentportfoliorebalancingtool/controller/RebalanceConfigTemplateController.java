package com.example.investmentportfoliorebalancingtool.controller;

import com.example.investmentportfoliorebalancingtool.domain.*;
import com.example.investmentportfoliorebalancingtool.service.RebalanceConfigTemplateService;
import com.example.investmentportfoliorebalancingtool.service.UserService;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class RebalanceConfigTemplateController {

    private final UserService userService;
    private final RebalanceConfigTemplateService rebalanceConfigTemplateService;

    public RebalanceConfigTemplateController(UserService userService, RebalanceConfigTemplateService rebalanceConfigTemplateService) {
        this.userService = userService;
        this.rebalanceConfigTemplateService = rebalanceConfigTemplateService;
    }

    // use command or form backing object instead of RebalanceConfigTemplate*() objects
    @GetMapping("/dashboard/rebalancing-templates/new")
    public String addNewRebalancingTemplate(@RequestParam("template_type") String templateType, Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);

            if(templateType.equals("Asset Class")) {
                model.addAttribute("rebalancing_template", new RebalanceConfigTemplateAssetClass());
                return "rebalancing-templates/form/asset_class";
            }
            else if(templateType.equals("Country")) {
                model.addAttribute("rebalancing_template", new RebalanceConfigTemplateCountry());
                return "rebalancing-templates/form/country";
            }
            else {
                model.addAttribute("rebalancing_template", new RebalanceConfigTemplateSector());
                return "rebalancing-templates/form/sector";
            }
        }
        else {
            throw new RuntimeException("Unable to fetch User Rebalancing Templates!");
        }
    }

    // use command or form backing object for respective entities instead of entity objects directly
    @PostMapping("/dashboard/rebalancing-templates")
    public String saveRebalancingTemplate(@RequestParam("template_type") String templateType,
                                          @RequestParam("name") String templateName,
                                          @ModelAttribute RebalanceConfigTemplateAssetClass templateAssetClass,
                                          @ModelAttribute RebalanceConfigTemplateCountry templateCountry,
                                          @ModelAttribute RebalanceConfigTemplateSector templateSector) {
        Optional<User> userOptional = userService.getAuthenticatedUser();

        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();
            try {
                // if template of same name for the user already exists
                if(rebalanceConfigTemplateService.existsForUser(authenticatedUser, templateName)) {
                    // throw error and catch to alert user
                    throw new RuntimeException("Rebalancing Template '" + templateName +  "' already exists." +
                                                "\nDelete the Template to add a new one or update the existing " +
                                                "Template to make any configuration changes.");
                }

                // Check number of existing user templates (max 10 can be stored)
                if (rebalanceConfigTemplateService.numberOfRebalancingTemplates(authenticatedUser) >= 10) {
                    // throw error and catch to alert user
                    throw new RuntimeException("Max limit (10) of Rebalancing Templates reached." +
                                                "\nDelete a Template to add a new one or update an existing Template.");
                }
                // Need a method to check if total percent of template = 100
                // and if less than 100 allocate the rest to unknown
                // if greater than 100 raise error/ alert user and don't add the template
                // else add the new template

                if(templateType.equals("Asset Class")) {
                    templateAssetClass.setType(RebalanceConfigTemplateType.ASSET_CLASS);

                    rebalanceConfigTemplateService.addRebalancingTemplate(authenticatedUser,
                            rebalanceConfigTemplateService.validateAssetClassTemplateAndSetUnknownAllocation(templateAssetClass));
                }
                else if(templateType.equals("Country")) {
                    templateCountry.setType(RebalanceConfigTemplateType.COUNTRY);

                    rebalanceConfigTemplateService.addRebalancingTemplate(authenticatedUser,
                            rebalanceConfigTemplateService.validateCountryTemplateAndSetUnknownAllocation(templateCountry));
                }
                else {
                    templateSector.setType(RebalanceConfigTemplateType.EQUITY_SECTOR);

                    rebalanceConfigTemplateService.addRebalancingTemplate(authenticatedUser,
                            rebalanceConfigTemplateService.validateSectorTemplateAndSetUnknownAllocation(templateSector));
                }

            }
            catch (Exception e) {
                if(e instanceof JdbcSQLIntegrityConstraintViolationException ||
                        e instanceof org.springframework.dao.DataIntegrityViolationException) {
                    System.out.println("Unable to add Rebalancing Template!");
                    System.out.println(e);
                }
                else {
                    System.out.println(e);
                }
            }
        }
        else {
            throw new RuntimeException("Unable to fetch User Rebalancing Templates!");
        }

        return "redirect:/dashboard/rebalancing-templates";
    }

    @GetMapping({"/dashboard/rebalancing-templates/{templateName}"})
    public String getRebalancingTemplate(@PathVariable String templateName,
                                         Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            Optional<RebalanceConfigTemplate> rebalanceConfigTemplateOptional = rebalanceConfigTemplateService
                    .getRebalancingTemplateFromName(authenticatedUser, templateName);

            model.addAttribute("user", authenticatedUser);

            if(rebalanceConfigTemplateOptional.isPresent()) {
                RebalanceConfigTemplate viewedRebalanceConfigTemplate = rebalanceConfigTemplateOptional.get();

                if(viewedRebalanceConfigTemplate instanceof RebalanceConfigTemplateAssetClass) {
                    model.addAttribute("rebalancing_template", viewedRebalanceConfigTemplate);
                    return "rebalancing-templates/view/asset_class";
                }
                else if(viewedRebalanceConfigTemplate instanceof RebalanceConfigTemplateCountry) {
                    model.addAttribute("rebalancing_template", viewedRebalanceConfigTemplate);
                    return "rebalancing-templates/view/country";
                }
                else if(viewedRebalanceConfigTemplate instanceof RebalanceConfigTemplateSector){
                    model.addAttribute("rebalancing_template", viewedRebalanceConfigTemplate);
                    return "rebalancing-templates/view/sector";
                }
                else {
                    throw new RuntimeException("Unable to fetch Rebalancing Template Details!");
                }

            }
            else {
                throw new RuntimeException("Unable to fetch Rebalancing Template Details!");
            }
        }
        else {
            throw new RuntimeException("Unable to fetch User Rebalancing Templates!");
        }
    }

    @GetMapping({"/dashboard/rebalancing-templates"})
    public String getAllRebalancingTemplates(Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            boolean disabled = rebalanceConfigTemplateService.numberOfRebalancingTemplates(authenticatedUser) >= 10;
            model.addAttribute("is_disabled", disabled);

            model.addAttribute("user", authenticatedUser);
            model.addAttribute("rebalancing_templates",
                    rebalanceConfigTemplateService.getRebalancingTemplates(authenticatedUser));
        }
        else {
            throw new RuntimeException("Unable to fetch User Rebalancing Templates!");
        }

        return "rebalancing-templates/index";
    }

    // HTML doesn't support HTTP DELETE
    @GetMapping({"/dashboard/rebalancing-templates/{templateName}/delete"})
    public String deleteRebalancingTemplate(@PathVariable String templateName) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if (userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            rebalanceConfigTemplateService.removeRebalancingTemplateByName(authenticatedUser, templateName);
        }
        else {
            throw new RuntimeException("Unable to fetch User Rebalancing Templates!");
        }

        return "redirect:/dashboard/rebalancing-templates";
    }
}
