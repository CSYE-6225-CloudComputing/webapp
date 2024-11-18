package com.mycompany.cloudproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.cloudproject.service.UserService;

@RestController
public class VerificationController {

     private static final Logger logger = LoggerFactory.getLogger(VerificationController.class.getName());


    @Autowired
    UserService userService;

    @RequestMapping(path = "/verify", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>>  verifyUser(@RequestParam("user") String email, @RequestParam("token") String token) {
       
        Map<String, String> response = new HashMap<>();

        if (email != null && token != null) {

            logger.info("In verify user method");

           boolean check =  userService.checkVerfication(email,token);
           if(check){
            logger.info("Verification successful for user: " + email);
            response.put("status", "success");
            response.put("message", "Verification successful for user: " + email);
            return new ResponseEntity<>(response, HttpStatus.OK); 
           }
           
            else {
                logger.info("Verification failed for user: " + email);
                response.put("status", "error");
                response.put("message", "Invalid verification request.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
 
            }
           
        }else{
            logger.info("Invalid verification request.");
            response.put("status", "error");
            response.put("message", "Invalid verification request.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); 
        }
    }
}
