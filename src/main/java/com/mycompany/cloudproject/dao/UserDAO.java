package com.mycompany.cloudproject.dao;

import com.mycompany.cloudproject.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class UserDAO{

    @PersistenceContext
    private EntityManager entityManager;


    public void createUser(User user) {
        entityManager.merge(user);
    }

    public User checkExistingUser(String email) {
        User existingUser = null;
        try {
            existingUser = entityManager.createQuery("SELECT u FROM User u where u.email = :email", User.class).setParameter("email", email).getSingleResult();
        } catch (Exception e) {

            return null;
        }

        return existingUser;
    }

    public void updateUser(User user) {
        entityManager.merge(user);
    }
}
