package com.mycompany.cloudproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.cloudproject.service.UserService;

@RestController
public class VerificationController {

    @Autowired
    UserService userService;

    @RequestMapping(path = "/verify", method = RequestMethod.GET)
    public String verifyUser(@RequestParam("user") String email, @RequestParam("token") String token) {
        // Simulate verification logic
        if (email != null && token != null) {

           boolean check =  userService.checkVerfication(email,token);
           if(check)
            return "Verification successful for user: " + email;
            else 
            return "Invalid verification request.";
        }else{
            return "Invalid verification request.";
        }
    }
}
