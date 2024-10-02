package com.mycompany.cloudproject.service;

import com.mycompany.cloudproject.controller.UserController;
import com.mycompany.cloudproject.dao.UserDAO;
import com.mycompany.cloudproject.dto.UserDTO;
import com.mycompany.cloudproject.exceptions.UnAuthorizedException;
import com.mycompany.cloudproject.exceptions.UserCustomExceptions;
import com.mycompany.cloudproject.model.User;
import com.mycompany.cloudproject.utilities.EncryptionUtility;
import com.mycompany.cloudproject.utilities.RequestCheckUtility;
import com.mycompany.cloudproject.utilities.TokenUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());


    @Autowired
    UserDAO userDAO;

    @Transactional
    public UserDTO createUser(UserDTO userDTO, HttpServletRequest request) throws Exception {

        logger.info("Creating user");
        if (!RequestCheckUtility.checkForParameterMap(request) || request.getHeader("Authorization") != null) {
            throw new UserCustomExceptions("Bad request");
        }

        User existingUser = userDAO.checkExistingUser(userDTO.getEmail());

        if (existingUser != null) {
            logger.error("User already exists");
            throw new UserCustomExceptions("User already exists");
        } else {
            User user = new User();
            userDTO.setAccountUpdated(user.getAccountUpdated());
            userDTO.setAccountCreated(user.getAccountCreated());
            userDTO.setId(user.getId());
            userDTO.setPassword(EncryptionUtility.getEncryptedPassword(userDTO.getPassword()));
            BeanUtils.copyProperties(userDTO, user);
            userDAO.createUser(user);
            BeanUtils.copyProperties(user, userDTO);

        }
        return userDTO;
    }

    public UserDTO getUserDetails(HttpServletRequest request) throws Exception {

        if (request.getContentLength() > 0)
            throw new UserCustomExceptions("Bad request");

        if (!request.getParameterMap().isEmpty())
            throw new UserCustomExceptions("Bad request");

        if (request.getHeader("Authorization") == null)
            throw new UnAuthorizedException("Unauthorized");

        UserDTO userDTO = new UserDTO();
        String token = request.getHeader("Authorization");

        if (token != null && !token.startsWith("Basic ")) {
            throw new UnAuthorizedException("Unauthorized");
        }

        if (token == null || token.isEmpty())
            throw new UnAuthorizedException("Unauthorized");

        Map<String, String> map = TokenUtility.getCredentials(token);
        String email = map.get("email");
        String password = map.get("password");
        //System.out.println("***password" + password);
        User existinguser = userDAO.checkExistingUser(email);
        if (existinguser == null || !EncryptionUtility.getDecryptedPassword(password, existinguser.getPassword())) {
            throw new UnAuthorizedException("Error occurred while validating credentials");
        }

        if (existinguser != null && EncryptionUtility.getDecryptedPassword(password, existinguser.getPassword())) {
            BeanUtils.copyProperties(existinguser, userDTO);
            return userDTO;
        }

        return null;

    }

    public void updateUserDetails(UserDTO userDTO, HttpServletRequest request) throws UserCustomExceptions, UnAuthorizedException {

        //check for proper request
        logger.info("checkig update user request");
        if (!RequestCheckUtility.checkForParameterMap(request) || !RequestCheckUtility.checkValidBasicAuthHeader(request)) {
            throw new UserCustomExceptions("Bad request");
        }
        String token = request.getHeader("Authorization");

        Map<String, String> map = TokenUtility.getCredentials(token);
        String email = map.get("email");
        String password = map.get("password");

        logger.info("UPDATE request : checking existing user");
        //check if user already exists or not
        User existinguser = userDAO.checkExistingUser(email);

        if (existinguser == null || !EncryptionUtility.getDecryptedPassword(password, existinguser.getPassword())) {
            throw new UnAuthorizedException("Error occurred while validating credentials");
        }

        if (password != null && existinguser != null) {
            existinguser.setPassword(EncryptionUtility.getEncryptedPassword(userDTO.getPassword()));
            existinguser.setAccountUpdated(LocalDateTime.now());
            // existinguser.setEmail(email);
            existinguser.setFirstName(userDTO.getFirstName());
            existinguser.setLastName(userDTO.getLastName());
            userDAO.updateUser(existinguser);
        }


    }
}
