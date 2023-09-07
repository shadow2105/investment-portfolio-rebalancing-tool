package com.example.investmentportfoliorebalancingtool.domain.dao;

import com.example.investmentportfoliorebalancingtool.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void create(User user) {
        entityManager.persist(user);
    }

    @Override
    public User read(UUID id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User read(String userName) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.userName = :username", User.class)
                    .setParameter("username", userName)
                    .getSingleResult();
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public Set<User> readAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultStream().collect(Collectors.toSet());
    }

    @Override
    public User update(User user) {
        return entityManager.merge(user);
    }

    @Override
    public void delete(User user) {
        entityManager.remove(user);
    }
}
