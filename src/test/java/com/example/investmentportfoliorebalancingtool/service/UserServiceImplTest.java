package com.example.investmentportfoliorebalancingtool.service;

import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.domain.UserRole;
import com.example.investmentportfoliorebalancingtool.domain.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String USERNAME = "ashwinrajput66@gmail.com";
    public static final UUID ID = UUID.fromString("1122cec6-72cc-4a13-b42d-450e21b1aa0d");

    @Mock
    UserRepository userRepository;
    @Mock
    AuthService authService;

    @InjectMocks
    UserServiceImpl userService;

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

    // When the user successfully authenticated (principal is not null), is not new (exists in local DB),
    // and is successfully updated
    @Test()
    void getAuthenticatedUserOld() {
        // Simulating principal as returned by method getPrincipal();
        User principal = new User();
        principal.setUserName(USERNAME);
        // Arbitrarily setting user role
        principal.setRole(UserRole.USER);

        when(authService.getPrincipal()).thenReturn(principal);

        // Since (old) user already exists
        user.trackNotNew();
        user.setCreatedBy(USERNAME);
        user.setCreatedAt(new Timestamp(123432452L));
        when(userRepository.findByUserName(anyString()))
                .thenReturn(Optional.of(user));

        // User retrieved from local DB is the same user which is being saved (updated)
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        // Assert that same (old) user is returned after being updated
        assertTrue(authenticatedUser.isPresent());
        assertEquals(ID, authenticatedUser.get().getId());

        // Assert that (old) user role got updated and is equal to that of principal
        assertNotNull(authenticatedUser.get().getRole());
        assertEquals(principal.getRole(), authenticatedUser.get().getRole());

        // Assert that (old) user updatedBy got updated and is equal to that of principal username,
        // and its own createdBy
        assertNotNull(authenticatedUser.get().getUpdatedBy());
        assertEquals(principal.getUserName(),
                authenticatedUser.get().getUpdatedBy());
        assertEquals(authenticatedUser.get().getCreatedBy(),
                authenticatedUser.get().getUpdatedBy());

        // Assert that (old) user updatedAt got updated and is not equal to its own createdAt
        // Update of user's updatedAt field requires dependency on Spring Data JPA or Hibernate
        // Hence requires an Integration Test
        /*
        * assertNotNull(authenticatedUser.get().getUpdatedAt());
        * assertNotEquals(authenticatedUser.get().getCreatedAt(),
        *       authenticatedUser.get().getUpdatedAt());
        */

        verify(userRepository, times(1)).findByUserName(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // When the user successfully authenticated (principal is not null), and is new (does not exist in local DB),
    // and is successfully saved
    @Test
    void getAuthenticatedUserNew() {
        // Simulating principal as returned by method getPrincipal();
        User principal = new User();
        principal.setUserName(USERNAME);
        // Arbitrarily setting user role
        principal.setRole(UserRole.USER);

        when(authService.getPrincipal()).thenReturn(principal);

        when(userRepository.findByUserName(anyString()))
                .thenReturn(Optional.empty());

        principal.setId(ID);
        when(userRepository.save(any(User.class)))
                .thenReturn(principal);

        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        // Assert that (new) user is returned after being saved
        assertTrue(authenticatedUser.isPresent());
        // Update of (new) user's id field requires dependency on Hibernate
        // Hence requires an Integration Test
        // assertNotNull(authenticatedUser.get().getId());
        assertEquals(principal.getUserName(), authenticatedUser.get().getUserName());

        // Assert that (new) user role got saved and is equal to that of principal
        assertNotNull(authenticatedUser.get().getRole());
        assertEquals(principal.getRole(), authenticatedUser.get().getRole());

        // Assert that (new) user createdBy got saved and is equal to that of principal username,
        // and its own updatedBy is null
        assertNotNull(authenticatedUser.get().getCreatedBy());
        assertEquals(principal.getUserName(),
                authenticatedUser.get().getCreatedBy());
        assertNull(authenticatedUser.get().getUpdatedBy());

        // Assert that (new) user createdAt got saved
        // Update of user's createdAt field requires dependency on Spring Data JPA or Hibernate
        // Hence requires an Integration Test
        // assertNotNull(authenticatedUser.get().getCreatedAt());

        verify(userRepository, times(1)).findByUserName(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getRegisteredUsers() {
        Set<User> users = new HashSet<>();
        User user1 = new User();
        User user2 = new User();

        users.add(user1);
        users.add(user2);

        when(userRepository.findAll())
                .thenReturn(users);

        Set<User> returnedUsers = userService.getRegisteredUsers();

        // Expecting a non-null Set of User having size 2
        // Redundant null check since method under test will always initialize a Set<User> registeredUsers
        assertNotNull(returnedUsers);
        assertEquals(2, returnedUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void removeRegisteredUserByIdFound() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(user));

        userService.removeRegisteredUserById(ID);

        // Redundant
        verify(userRepository, times(1)).findById(any(UUID.class));

        // Expecting a single invocation of userRepository.remove(User user),
        // since assumed that the user to be deleted is found by specified id equals ID
        verify(userRepository, times(1)).remove(any(User.class));
    }

    @Test
    void removeRegisteredUserByIdNotFound() {
        when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        userService.removeRegisteredUserById(ID);

        // Redundant
        verify(userRepository, times(1)).findById(any(UUID.class));

        // Expecting no invocation of userRepository.remove(User user),
        // since assumed that the user to be deleted is not found by specified id equals ID
        verify(userRepository, times(0)).remove(any(User.class));
    }
}