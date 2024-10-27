package com.mycompany.cloudproject.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mycompany.cloudproject.model.Image;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ImageDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public String createImage(Image image) {
        entityManager.persist(image); // Use persist to save the new entity
        return image.getId(); // Return the ID of the newly created image
    }

    public Optional<Image> getImageByUserId(String userId) {
        // Using a native SQL query to fetch the image by user ID
        String query = "SELECT * FROM images WHERE user_id = :userId";
        List<Image> result = entityManager.createNativeQuery(query, Image.class)
                .setParameter("userId", userId)
                .getResultList();

        return result.stream().findFirst(); // Return the first result as an Optional
    }

    public List<Image> getImagesByUserId(String userId) {
        // Using a native SQL query to fetch all images by user ID
        String query = "SELECT * FROM images WHERE user_id = :userId";
        return entityManager.createNativeQuery(query, Image.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void deleteImagesByUserId(String userId) {
        // Deleting images by user ID using a native SQL query
        String deleteQuery = "DELETE FROM images WHERE user_id = :userId";
        entityManager.createNativeQuery(deleteQuery)
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
