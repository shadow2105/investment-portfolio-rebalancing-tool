package com.example.investmentportfoliorebalancingtool.domain.repositories;

import com.example.investmentportfoliorebalancingtool.domain.AdminProfile;
import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.domain.UserRole;
import com.example.investmentportfoliorebalancingtool.domain.dao.UserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    public static final String USERNAME = "ashwinrajput66@gmail.com";
    public static final UUID ID = UUID.fromString("1122cec6-72cc-4a13-b42d-450e21b1aa0d");

    @Mock
    UserDAO userDAO;

    @InjectMocks
    UserRepositoryImpl userRepository;

    User user;
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(ID);
        user.setUserName(USERNAME);
    }

    @AfterEach
    void tearDown() {
        //user = null;
    }

    // When the user is not null, is new and not an admin
    @Test
    void saveNewUser() {
        User userToSave = new User();
        // Properties of new user
        userToSave.setId(ID);
        userToSave.setRole(UserRole.USER);

        User savedUser = userRepository.save(userToSave);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserProfile());
        assertNotNull(savedUser.getUserProfile().getInvestmentAccounts());
        assertNotNull(savedUser.getUserProfile().getRebalanceConfigTemplates());
        assertNotNull(savedUser.getUserProfile().getWidgets());
        assertNull(savedUser.getAdminProfile());

        verify(userDAO, times(1)).create(any(User.class));
        verify(userDAO, times(0)).update(any(User.class));
    }

    // When the user is not null, is new and an admin
    @Test
    void saveNewAdmin() {
        User userToSave = new User();
        // Properties of new user (admin)
        userToSave.setId(ID);
        userToSave.setRole(UserRole.ADMIN);

        User savedUser = userRepository.save(userToSave);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserProfile());
        assertNotNull(savedUser.getUserProfile().getInvestmentAccounts());
        assertNotNull(savedUser.getUserProfile().getRebalanceConfigTemplates());
        assertNotNull(savedUser.getUserProfile().getWidgets());
        assertNotNull(savedUser.getAdminProfile());

        verify(userDAO, times(1)).create(any(User.class));
        verify(userDAO, times(0)).update(any(User.class));
    }

    // When the user is not null, is not new, not an admin and does have an admin profile
    // that is, when the user role is changed from Admin to User
    @Test
    void saveOldUser() {
        User userToSave = new User();
        // Properties of old user
        userToSave.setId(ID);
        userToSave.trackNotNew();
        userToSave.setRole(UserRole.USER);

        when(userDAO.update(any(User.class)))
                .thenReturn(user);

        User savedUser = userRepository.save(userToSave);

        assertNotNull(savedUser);
        assertNull(savedUser.getAdminProfile());
        assertEquals(ID, savedUser.getId());

        verify(userDAO, times(0)).create(any(User.class));
        verify(userDAO, times(1)).update(any(User.class));
    }

    // When the user is not null, is not new, an admin and does not have an admin profile
    // that is, when the user role is changed from User to Admin
    @Test
    void saveOldAdmin() {
        User userToSave = new User();
        // Properties of old user (admin)
        userToSave.setId(ID);
        userToSave.trackNotNew();
        userToSave.setRole(UserRole.ADMIN);

        // User returned after userDao.update(User user) is executed, will have an admin profile
        user.setAdminProfile(new AdminProfile(user));
        when(userDAO.update(any(User.class)))
                .thenReturn(user);

        User savedUser = userRepository.save(userToSave);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getAdminProfile());
        assertEquals(ID, savedUser.getId());

        verify(userDAO, times(0)).create(any(User.class));
        verify(userDAO, times(1)).update(any(User.class));
    }

    // When the user is found
    @Test
    void findById() {
        // Define behavior/return value of a method of mock object
        when(userDAO.read(any(UUID.class)))
                .thenReturn(user);

        Optional<User> returnedUser = userRepository.findById(ID);

        // Expecting an Optional with User having id equals ID
        assertTrue(returnedUser.isPresent());
        assertEquals(ID, returnedUser.get().getId());
    }

    // When the user is not found
    @Test
    void findByIdNotFound() {
        when(userDAO.read(any(UUID.class)))
                .thenReturn(null);

        Optional<User> returnedUser = userRepository.findById(ID);

        // Expecting an empty Optional
        assertTrue(returnedUser.isEmpty());
    }

    // When the user is found
    @Test
    void findByUserName() {
        when(userDAO.read(anyString()))
                .thenReturn(user);

        Optional<User> returnedUser = userRepository
                .findByUserName(USERNAME);

        // Expecting an Optional with User having username equals USERNAME
        assertTrue(returnedUser.isPresent());
        assertEquals(USERNAME, returnedUser.get().getUserName());
    }

    // When the user is not found
    @Test
    void findByUserNameNotFound() {
        when(userDAO.read(anyString()))
                .thenReturn(null);

        Optional<User> returnedUser = userRepository
                .findByUserName(USERNAME);

        // Expecting an empty Optional
        assertTrue(returnedUser.isEmpty());
    }

    @Test
    void findAll() {
        Set<User> users = new HashSet<>();
        User user1 = new User();
        User user2 = new User();

        users.add(user1);
        users.add(user2);

        when(userDAO.readAll())
                .thenReturn(users);

        Set<User> returnedUsers = userRepository.findAll();

        // Expecting a non-null Set of User having size 2
        assertNotNull(returnedUsers);
        assertEquals(2, returnedUsers.size());
        verify(userDAO, times(1)).readAll();
    }

    @Test
    void remove() {
        userRepository.remove(user);

        // Expecting a single invocation of userDao.delete(User user) method
        verify(userDAO, times(1)).delete(any());
    }
}