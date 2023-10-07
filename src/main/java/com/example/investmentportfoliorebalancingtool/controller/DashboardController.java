package com.example.investmentportfoliorebalancingtool.controller;

import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@Controller
public class DashboardController {
    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping({"/dashboard"})
    public String getDashboard(Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);
        }
        else {
            throw new RuntimeException("Unable to fetch User Dashboard!");
        }

        return "dashboard";
    }

    @GetMapping({"/admin-dashboard"})
    @PreAuthorize("hasAuthority('admin')")
    public String getAdminDashboard(Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);
            model.addAttribute("registered_users", userService.getRegisteredUsers());

        }
        else {
            throw new RuntimeException("Unable to fetch Admin Dashboard!");
        }

        return "admin_dashboard";
    }

    // HTML doesn't support HTTP DELETE
    @GetMapping({"/admin-dashboard/registered-users/{id}/delete"})
    @PreAuthorize("hasAuthority('admin')")
    public String deleteRegisteredUser(@PathVariable String id) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if (userOptional.isPresent()) {
            // This only deletes the user info from Database.
            // Need to deactivate (not delete) user from okta separately using Okta API;
            // https://developer.okta.com/docs/reference/api/users/#response-example-29
            // POST /api/v1/users/${userId}/lifecycle/deactivate
            // DELETE /api/v1/users/${userId}

            /* Need a separate function/job which runs periodically to check if a user has not logged
             * from a long time (say 1 year; see updated at) and delete such user entries from local db
             * after deactivating them from Okta
             */

            //Case 1: User deactivated from Okta, but exists in local DB (above described job will handle this)
            //Case 2: User deleted from local DB will lead to instant deactivation on Okta (using Okta API)
            userService.removeRegisteredUserById(UUID.fromString(id));
        }
        else {
            throw new RuntimeException("Unable to fetch User Investment Accounts!");
        }

        return "redirect:/admin-dashboard";
    }
}


// https://mkyong.com/spring-mvc/spring-mvc-exceptionhandler-example/