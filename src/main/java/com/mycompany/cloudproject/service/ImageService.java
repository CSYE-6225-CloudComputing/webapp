package com.mycompany.cloudproject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.mycompany.cloudproject.dao.ImageDAO;
import com.mycompany.cloudproject.dao.UserDAO;
import com.mycompany.cloudproject.dto.ImageResponseDTO;

import com.mycompany.cloudproject.exceptions.UnAuthorizedException;
import com.mycompany.cloudproject.exceptions.UserCustomExceptions;
import com.mycompany.cloudproject.exceptions.NotFoundException;
import com.mycompany.cloudproject.model.Image;
import com.mycompany.cloudproject.model.User;
import com.mycompany.cloudproject.utilities.EncryptionUtility;
import com.mycompany.cloudproject.utilities.RequestCheckUtility;
import com.mycompany.cloudproject.utilities.TokenUtility;
import com.timgroup.statsd.StatsDClient;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ImageService {

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private ImageDAO imageDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private StatsDClient statsd;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class.getName());

    public ImageResponseDTO uploadProfilePic(MultipartFile file, HttpServletRequest request)
            throws Exception {
        logger.info("checkig update user request FOR USER PROFILE");
        

        if (request.getHeader("Authorization") == null)
            throw new UnAuthorizedException("Unauthorized");

        if (!RequestCheckUtility.checkForParameterMap(request)
                || !RequestCheckUtility.checkValidBasicAuthHeader(request)) {
            throw new UserCustomExceptions("Bad request");
        }
        String token = request.getHeader("Authorization");

        Map<String, String> map = TokenUtility.getCredentials(token);
        String email = map.get("email");
        String password = map.get("password");

        logger.info("POST USER PROFILE request : checking existing user");

        User existinguser = userDAO.checkExistingUser(email);

        if (existinguser == null || !EncryptionUtility.getDecryptedPassword(password, existinguser.getPassword())) {
            throw new UnAuthorizedException("Error occurred while validating credentials");
        }

        System.out.println("File Content Type: " + file.getContentType());
        String contentType = file
                .getContentType();
        if (!contentType.equalsIgnoreCase("image/png") && !contentType.equalsIgnoreCase("image/jpg")
                && !contentType.equalsIgnoreCase("image/jpeg")) {
            System.out.println("inappropriate");

        }

        if (existinguser != null) {
            Optional<Image> optionalImage = imageDAO.getImageByUserId(existinguser.getId());
            if (optionalImage.isPresent()) {
                logger.error("Image is already Present");
                throw new UserCustomExceptions("Image is already Present");
            }
        }

        return uploadImage(file, existinguser);
    }

    public ImageResponseDTO uploadImage(MultipartFile file, User user) throws Exception {

        System.out.println("File Content Type: " + file.getContentType());
        String contentType = file.getContentType();
        if (!contentType.equalsIgnoreCase("image/png") &&
                !contentType.equalsIgnoreCase("image/jpg") &&
                !contentType.equalsIgnoreCase("image/jpeg")) {
            System.out.println("inappropriate");
            logger.error("endpoint.image.self.api.post Forbidden Act");
            throw new IllegalArgumentException("Invalid image type");

        }
        // Validate file type
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image type");
        }

        String key = "users/" + user.getId() + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Upload to S3
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        long startTimes3 = getCurrentTimeMillis();

        s3Client.putObject(bucketName, user.getId() + "/" + file.getOriginalFilename(), file.getInputStream(),
                metadata);

        logExecutionTime("s3.postUserProfile.execution.time", startTimes3);

        Image image = new Image();
        image.setUserId(user.getId());
        image.setFileName(file.getOriginalFilename());
        image.setUrl(s3Client.getUrl(bucketName, key).toString());
        image.setContentType(file.getContentType());

        String formattedUrl = bucketName + "/" + user.getId() + "/" + file.getOriginalFilename();
        image.setUrl(formattedUrl);

        long startTime = getCurrentTimeMillis();

        String generatedId = imageDAO.createImage(image);

        logExecutionTime("db.postUserProfile.execution.time", startTime);

        System.out.println("***" + generatedId);

        ImageResponseDTO response = new ImageResponseDTO(
                image.getFileName(),
                generatedId,
                image.getUrl(),
                image.getUploadDate(),
                user.getId());

        return response;
    }

    public ImageResponseDTO getProfileDetails(HttpServletRequest request)
            throws UnAuthorizedException, UserCustomExceptions, NotFoundException {

        ImageResponseDTO imageResponseDTO = null;
        if (request.getContentLength() > 0)
            throw new UserCustomExceptions("Bad request");

        if (!request.getParameterMap().isEmpty())
            throw new UserCustomExceptions("Bad request");

        if (request.getHeader("Authorization") == null)
            throw new UnAuthorizedException("Unauthorized");

        // ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
        String token = request.getHeader("Authorization");

        if (token != null && !token.startsWith("Basic ")) {
            throw new UnAuthorizedException("Unauthorized");
        }

        if (token == null || token.isEmpty())
            throw new UnAuthorizedException("Unauthorized");

        Map<String, String> map = TokenUtility.getCredentials(token);
        String email = map.get("email");
        String password = map.get("password");
        System.out.println("***password" + password);
        User existinguser = userDAO.checkExistingUser(email);
        if (existinguser == null || !EncryptionUtility.getDecryptedPassword(password, existinguser.getPassword())) {
            throw new UnAuthorizedException("Error occurred while validating credentials");
        }

        if (existinguser != null && EncryptionUtility.getDecryptedPassword(password, existinguser.getPassword())) {

            long startTime = getCurrentTimeMillis();

            Optional<Image> optionalImage = imageDAO.getImageByUserId(existinguser.getId()); // Implement this in your
                                                                                             // DAO
            logExecutionTime("db.getUserProfile.execution.time", startTime);

            if (!optionalImage.isPresent()) {
                throw new NotFoundException("No image found for this user");
            }

            Image image = optionalImage.get();

            imageResponseDTO = new ImageResponseDTO(
                    image.getFileName(),
                    image.getId(),
                    image.getUrl(),
                    image.getUploadDate(),
                    existinguser.getId());

        }

        return imageResponseDTO;
    }

    public void deleteAllImagesForUser(HttpServletRequest request) throws UnAuthorizedException, UserCustomExceptions, NotFoundException {
        logger.info("Checking delete user images request");

        if (request.getContentLength() > 0)
            throw new UserCustomExceptions("Bad request");

        if (!request.getParameterMap().isEmpty())
            throw new UserCustomExceptions("Bad request");

        if (request.getHeader("Authorization") == null)
            throw new UnAuthorizedException("Unauthorized");

        if (!RequestCheckUtility.checkForParameterMap(request)
                || !RequestCheckUtility.checkValidBasicAuthHeader(request)) {
            throw new UserCustomExceptions("Bad request");
        }

        String token = request.getHeader("Authorization");
        Map<String, String> map = TokenUtility.getCredentials(token);
        String email = map.get("email");
        String password = map.get("password");

        User existingUser = userDAO.checkExistingUser(email);
        if (existingUser == null || !EncryptionUtility.getDecryptedPassword(password, existingUser.getPassword())) {
            throw new UnAuthorizedException("Error occurred while validating credentials");
        }

        List<Image> userImages = null;
        userImages = imageDAO.getImagesByUserId(existingUser.getId());
        if (userImages==null ||  userImages.isEmpty()) {
            logger.info("No images found for user");
            throw new NotFoundException("No images found for this user.");
        } else {

            deleteImagesFromS3(userImages);

            // Delete images from database
            long startTime = getCurrentTimeMillis();
            imageDAO.deleteImagesByUserId(existingUser.getId());
            logExecutionTime("db.deleteUserProfile.execution.time", startTime);
            logger.info("Successfully deleted all images for user: " + existingUser.getId());

        }

    }

    private void deleteImagesFromS3(List<Image> images) throws UserCustomExceptions {
        List<DeleteObjectsRequest.KeyVersion> objectsToDelete = new ArrayList<>();

        for (Image image : images) {
            String key = extractS3KeyFromUrl(image.getUrl());
            objectsToDelete.add(new DeleteObjectsRequest.KeyVersion(key));
        }

        if (!objectsToDelete.isEmpty()) {
            try {
                // Create the delete request using the list directly
                DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName)
                        .withKeys(objectsToDelete);
                long startTime = getCurrentTimeMillis();
                DeleteObjectsResult result = s3Client.deleteObjects(deleteRequest);
                logExecutionTime("s3.deleteUserProfile.execution.time", startTime);

                logger.info("Deleted images from S3 bucket: " + bucketName);
            } catch (MultiObjectDeleteException e) {
                // Handle any delete failures if needed
                logger.error("Error occurred while deleting images from S3: ", e);
            }
        } else {
            logger.warn("No images to delete from S3.");
            throw new UserCustomExceptions("No images found for this user.");
        }
    }

    private String extractS3KeyFromUrl(String url) {
        int bucketNameLength = bucketName.length() + 1;
        return url.substring(bucketNameLength);
    }

    private long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void logExecutionTime(String metricName, long startTime) {
        long endTime = System.currentTimeMillis();
        statsd.recordExecutionTime(metricName, endTime - startTime);
    }

}
