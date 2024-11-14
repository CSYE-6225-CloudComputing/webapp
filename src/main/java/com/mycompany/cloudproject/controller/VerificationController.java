package com.mycompany.cloudproject.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String verifyUser(@RequestParam("user") String email, @RequestParam("token") String token) {
        // Simulate verification logic

        if (email != null && token != null) {

            logger.info("In verify user method");

           boolean check =  userService.checkVerfication(email,token);
           if(check){
            logger.info("Verification successful for user: " + email);

            return "Verification successful for user: " + email;
           }
           
            else {
                logger.info("Verification failed for user: " + email);
                return "Invalid verification request.";
 
            }
           
        }else{
            logger.info("Invalid verification request.");
            return "Invalid verification request.";
        }
    }
}
