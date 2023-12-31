package com.example.investmentportfoliorebalancingtool.domain.repositories;

import com.example.investmentportfoliorebalancingtool.domain.*;
import com.example.investmentportfoliorebalancingtool.domain.dao.UserDAO;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserDAO userDAO;

    public UserRepositoryImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    @Override
    public User save(User user) {
        if (user != null) {
            if(user.isNew()) {
                UserProfile userProfile = new UserProfile(user);
                user.setUserProfile(userProfile);

                userProfile.setInvestmentAccounts(Collections.synchronizedSet(new LinkedHashSet<InvestmentAccount>()));
                userProfile.setRebalanceConfigTemplates(Collections.synchronizedSet(new LinkedHashSet<RebalanceConfigTemplate>()));
                userProfile.setWidgets(Collections.synchronizedSet(new LinkedHashSet<Widget>()));

                if(user.getRole().equals(UserRole.ADMIN)) {
                    AdminProfile adminProfile = new AdminProfile(user);
                    user.setAdminProfile(adminProfile);
                }
                userDAO.create(user);
            }
            else {
                AdminProfile adminProfile = user.getAdminProfile();

                // When the user role is changed from User to Admin
                if(adminProfile == null && user.getRole().equals(UserRole.ADMIN)) {
                    adminProfile = new AdminProfile(user);
                    user.setAdminProfile(adminProfile);
                }
                // When the user role is changed from Admin to User
                else if(adminProfile != null && !user.getRole().equals(UserRole.ADMIN)) {
                    adminProfile.setUser(null);
                    user.setAdminProfile(null);
                }
                return userDAO.update(user);
            }
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userDAO.read(id));
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return Optional.ofNullable(userDAO.read(userName));
    }

    @Override
    public Set<User> findAll() {
        return userDAO.readAll();
    }

    @Override
    public void remove(User user) {
        if (user != null) {
            userDAO.delete(user);
        }
    }
}
