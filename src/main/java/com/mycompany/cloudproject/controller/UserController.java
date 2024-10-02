package com.mycompany.cloudproject.controller;


import com.mycompany.cloudproject.model.User;
import com.mycompany.cloudproject.dto.UserDTO;
import com.mycompany.cloudproject.service.UserService;
import com.mycompany.cloudproject.utilities.RequestCheckUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class.getName());


    @Autowired
    UserService userService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostMapping("/v1/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("POST: Request received");
        setResponseHeaders(response);
        if (bindingResult.hasErrors()) {
            logger.error("Post Request : " + bindingResult.getFieldError().getDefaultMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserDTO dto = userService.createUser(userDTO, request);

        logger.info("POST: Request completed");
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);

    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<UserDTO> getUserDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("GET: Request received");
        setResponseHeaders(response);
        if (!RequestCheckUtility.checkRequestBody(request)) {
            UserDTO userDTO = userService.getUserDetails(request);

            logger.info("GET: Response received");

            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            logger.error("GET : request failed due to bad request");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<Void> updateUser(@RequestBody(required = false) @Valid UserDTO userDTO, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            //Map<String, Object> map = setResponse(user);
            logger.info("PUT : Update Response completed");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }

        logger.info("PUT : updating failed due to : bad request");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(path = "/v1/user", method = {RequestMethod.GET, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void unSupportedMethods(HttpServletResponse response) {
        logger.error("Unsupported HTTP method");
        setResponseHeaders(response);
    }

    @RequestMapping(path = "/v1/user/self", method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void unSupportedMethodsForGet(HttpServletResponse response) {
        logger.error("Unsupported HTTP method");
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

