package com.mycompany.cloudproject.controller;


import com.mycompany.cloudproject.dto.ImageResponseDTO;
import com.mycompany.cloudproject.dto.UserDTO;
import com.mycompany.cloudproject.service.ImageService;
import com.mycompany.cloudproject.service.UserService;
import com.mycompany.cloudproject.utilities.RequestCheckUtility;
import com.timgroup.statsd.NonBlockingStatsDClient;
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


import java.util.LinkedHashMap;
import java.util.Map;


@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class.getName());
    private static final StatsDClient statsd = new NonBlockingStatsDClient("cloudproject", "localhost", 8125);


    @Autowired
    UserService userService;

    @Autowired
    ImageService imageService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostMapping("/v1/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        long startTime = System.currentTimeMillis();
        statsd.incrementCounter("createUser.request.count");
                
        logger.info("POST: Request received");
        setResponseHeaders(response);
        if (bindingResult.hasErrors()) {
            logger.error("Post Request : " + bindingResult.getFieldError().getDefaultMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserDTO dto = userService.createUser(userDTO, request);

        long endTime = System.currentTimeMillis();
        statsd.recordExecutionTime("createUser.execution.time", endTime - startTime);


        logger.info("POST: Request completed");
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);

    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<UserDTO> getUserDetails(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        long startTime = System.currentTimeMillis();
        statsd.incrementCounter("api.getUserDetails.count");        
        logger.info("GET: Request received");
        setResponseHeaders(response);
        if (!RequestCheckUtility.checkRequestBody(request)) {
            UserDTO userDTO = userService.getUserDetails(request);

            logger.info("GET: Response received");
            long endTime = System.currentTimeMillis();
            statsd.recordExecutionTime("getUserDetails.execution.time", endTime - startTime);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            long endTime = System.currentTimeMillis();
            statsd.recordExecutionTime("getUserDetails.execution.time", endTime - startTime);
            logger.error("GET : request failed due to bad request");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

       
 
    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<Void> updateUser(@RequestBody(required = false) @Valid UserDTO userDTO,
            BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("PUT : Request Received");
        setResponseHeaders(response);

        if (!RequestCheckUtility.checkValidBasicAuthHeader(request))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (userDTO == null) {
            logger.error("PUT Request: Request body is missing");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (bindingResult.hasErrors()) {
            logger.error("PUT Request : " + bindingResult.getFieldError().getDefaultMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (RequestCheckUtility.checkRequestBody(request)) {
            userService.updateUserDetails(userDTO, request);
            // Map<String, Object> map = setResponse(user);
            logger.info("PUT : Update Response completed");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }

        logger.info("PUT : updating failed due to : bad request");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/v1/user/self/pic")
    public ResponseEntity<ImageResponseDTO> uploadProfilePic(@RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("POST: Profile picture upload request received");
        setResponseHeaders(response);

        if (file.isEmpty()) {
            logger.error("POST Request: No file uploaded");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            logger.error("POST Request: Unsupported file type");
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        ImageResponseDTO imageDTO = imageService.uploadProfilePic(file, request);

        logger.info("POST: Profile picture uploaded successfully");
        return new ResponseEntity<>(imageDTO, HttpStatus.OK);
    }

    @GetMapping("/v1/user/self/pic")
    public ResponseEntity<ImageResponseDTO> getImage(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
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
    }

    @DeleteMapping("/v1/user/self/pic")
    public ResponseEntity<Void> deleteAllImages(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        logger.info("DELETE: Request received to delete all images");
        setResponseHeaders(response);

        if (!RequestCheckUtility.checkValidBasicAuthHeader(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            imageService.deleteAllImagesForUser(request);
            logger.info("DELETE: All images deleted successfully");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("DELETE: Error occurred while deleting images - " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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

}
