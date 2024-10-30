package com.mycompany.cloudproject.controller;

import com.mycompany.cloudproject.dto.ImageResponseDTO;
import com.mycompany.cloudproject.dto.UserDTO;
import com.mycompany.cloudproject.exceptions.UnAuthorizedException;
import com.mycompany.cloudproject.service.ImageService;
import com.mycompany.cloudproject.service.UserService;
import com.mycompany.cloudproject.utilities.RequestCheckUtility;
import com.timgroup.statsd.StatsDClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class.getName());

    @Autowired
    UserService userService;

    @Autowired
    ImageService imageService;

    @Autowired
    private StatsDClient statsd;

    @PostMapping("/v1/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        long startTime = getCurrentTimeMillis();
        incrementRequestCount("api.createUser.request.count");

        try {

            logger.info("POST: Request received for User createion");
            setResponseHeaders(response);
            if (bindingResult.hasErrors()) {
                logger.error("Post Request for create new user: " + bindingResult.getFieldError().getDefaultMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            UserDTO dto = userService.createUser(userDTO, request);

            logger.info("POST: Request completed for user");
            return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
        } finally {
            logExecutionTime("api.createUser.execution.time", startTime);
        }

    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<UserDTO> getUserDetails(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        long startTime = getCurrentTimeMillis();
        incrementRequestCount("api.getUser.request.count");

        try {
            logger.info("GET USER INFORMATION: Request received");
            setResponseHeaders(response);
            if (!RequestCheckUtility.checkRequestBody(request)) {
                UserDTO userDTO = userService.getUserDetails(request);
                logger.info("GET USER INFORMATION: Response received for user creation");
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                logger.error("GET USER INFORMATION:: request failed due to bad request");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } finally {
            logExecutionTime("api.getUser.execution.time", startTime);
        }

    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<Void> updateUser(@RequestBody(required = false) @Valid UserDTO userDTO,
            BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws Exception {

        long startTime = System.currentTimeMillis();
        incrementRequestCount("api.putUserDetails.count");
        try {
            logger.info("PUT[UPDATE] USER INFORMATION: : Request Received");
            setResponseHeaders(response);

            if (!RequestCheckUtility.checkValidBasicAuthHeader(request)) {
                logger.error("PUT[UPDATE] USER INFORMATION: Bad Request");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (userDTO == null) {
                logger.error("PUT[UPDATE] USER INFORMATION Request body is missing");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                logger.error("PUT[UPDATE] USER INFORMATION : " + bindingResult.getFieldError().getDefaultMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (RequestCheckUtility.checkRequestBody(request)) {
                userService.updateUserDetails(userDTO, request);
                logger.info("PUT[UPDATE] USER INFORMATION : Update Response completed Successfully");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }

            logger.error("PUT[UPDATE] USER INFORMATION : updating failed due to : bad request");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            logExecutionTime("api.putUser.execution.time", startTime);
        }
    }

    @PostMapping("/v1/user/self/pic")
    public ResponseEntity<ImageResponseDTO> uploadProfilePic(@RequestParam("profilePic") MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long startTime = System.currentTimeMillis();
        incrementRequestCount("api.postUserProfile.count");
        try {
            logger.info("POST USER PROFILE UPLOAD REQUEST: Profile picture upload request received");
            setResponseHeaders(response);

             if (request.getHeader("Authorization") == null)
            throw new UnAuthorizedException("Unauthorized");
            
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            if (multipartRequest.getMultiFileMap().size() != 1
                    || !multipartRequest.getFileMap().containsKey("profilePic")) {
                logger.error("POST USER PROFILE UPLOAD REQUEST:: Invalid form data. Only 'profilePic' is allowed");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (!request.getParameterMap().isEmpty()) {
                logger.error("POST USER PROFILE UPLOAD REQUEST:: Query parameters are not allowed");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Check if the file is empty
            if (file == null || file.isEmpty()) {
                logger.error("POST USER PROFILE UPLOAD REQUEST:: No file uploaded");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String contentType = file.getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {

                logger.error("POST USER PROFILE UPLOAD REQUEST:: Unsupported file type");
                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }

            ImageResponseDTO imageDTO = imageService.uploadProfilePic(file, request);

            logger.info("POST USER PROFILE UPLOAD REQUEST: Profile picture uploaded successfully");
            return new ResponseEntity<>(imageDTO, HttpStatus.OK);
        } finally {
            logExecutionTime("api.postUserProfile.execution.time", startTime);
        }
    }

    @GetMapping("/v1/user/self/pic")
    public ResponseEntity<ImageResponseDTO> getImage(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        long startTime = System.currentTimeMillis();
        incrementRequestCount("api.getUserProfile.count");
        try {
            logger.info("GET: Request received for Image");
            setResponseHeaders(response);
            if (!RequestCheckUtility.checkRequestBody(request)) {
                ImageResponseDTO imageResponseDTO = imageService.getProfileDetails(request);
                logger.info("GET: Response received for images");
                return new ResponseEntity<>(imageResponseDTO, HttpStatus.OK);
            } else {
                logger.error("GET : IAMGE request failed due to bad request");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } finally {
            logExecutionTime("api.getUserProfile.execution.time", startTime);
        }
    }

    @DeleteMapping("/v1/user/self/pic")
    public ResponseEntity<Void> deleteAllImages(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        long startTime = System.currentTimeMillis();
        setResponseHeaders(response);
        incrementRequestCount("api.deleteUserProfile.count");
        try {
            logger.info("DELETE: Request received to delete all images");

            if (!RequestCheckUtility.checkValidBasicAuthHeader(request)) {
                logger.error("DELETE: Error occurred while deleting images - UNAUTORIZED");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        
                imageService.deleteAllImagesForUser(request);
                logger.info("DELETE: All images deleted successfully");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            
        } finally {
            logExecutionTime("api.deleteUserProfile.execution.time", startTime);
        }
    }

    @RequestMapping(path = "/v1/user", method = { RequestMethod.GET, RequestMethod.PATCH, RequestMethod.DELETE,
            RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.PUT })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void unSupportedMethods(HttpServletResponse response) {
        logger.error("Unsupported HTTP method FOR USER");
        setResponseHeaders(response);
    }

    @RequestMapping(path = "/v1/user/self/pic", method = { RequestMethod.HEAD, RequestMethod.PATCH,
            RequestMethod.OPTIONS,
            RequestMethod.PUT })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void unSupportedMethodsForUserspROFILE(HttpServletResponse response) {
        logger.error("Unsupported HTTP method for PIC");
        setResponseHeaders(response);
    }

    @RequestMapping(path = "/v1/user/self", method = { RequestMethod.HEAD, RequestMethod.PATCH, RequestMethod.OPTIONS,
            RequestMethod.HEAD, RequestMethod.DELETE })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void unSupportedMethodsForUsers(HttpServletResponse response) {
        logger.error("Unsupported HTTP method FOR USER");
        setResponseHeaders(response);
    }

    public void setResponseHeaders(HttpServletResponse response) {
        logger.info("setting response headers");
        response.setHeader("cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Content-Type-Options", "nosniff");
        logger.info("Finished setting response headers");
    }

    public Map<String, Object> setResponse(UserDTO user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getId());
        response.put("first_name", user.getFirstName());
        response.put("last_name", user.getLastName());
        response.put("email", user.getEmail());
        response.put("account_created", user.getAccountCreated());
        response.put("account_updated", user.getAccountUpdated());
        return response;
    }

    private void incrementRequestCount(String metricName) {
        statsd.incrementCounter(metricName);
    }

    private long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void logExecutionTime(String metricName, long startTime) {
        long endTime = System.currentTimeMillis();
        statsd.recordExecutionTime(metricName, endTime - startTime);
    }

}