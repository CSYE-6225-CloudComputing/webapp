package com.mycompany.cloudproject.dao;


import com.mycompany.cloudproject.model.User;
import com.mycompany.cloudproject.model.UserToken;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class UserTokenDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public UserToken findUserToken(User user, String token) {
        UserToken userToken = null;
        try {
            userToken = entityManager.createQuery(
                    "SELECT u FROM UserToken u WHERE u.user = :user AND u.token = :token", UserToken.class)
                    .setParameter("user", user)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (Exception e) {
            // Handle specific exceptions (e.g., NoResultException) if needed
            return null;
        }
    
        return userToken;
    }
    
}
