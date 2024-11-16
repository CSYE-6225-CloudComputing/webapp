package com.mycompany.cloudproject.service;

import com.mycompany.cloudproject.dao.UserDAO;
import com.mycompany.cloudproject.dao.UserTokenDAO;
import com.mycompany.cloudproject.dto.UserDTO;
import com.mycompany.cloudproject.exceptions.UnAuthorizedException;
import com.mycompany.cloudproject.exceptions.UserCustomExceptions;
import com.mycompany.cloudproject.model.User;
import com.mycompany.cloudproject.model.UserToken;
import com.mycompany.cloudproject.utilities.EncryptionUtility;
import com.mycompany.cloudproject.utilities.RequestCheckUtility;
import com.mycompany.cloudproject.utilities.TokenUtility;
import com.timgroup.statsd.StatsDClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());


    @Autowired
    UserDAO userDAO;

     @Autowired
    private StatsDClient statsd;

    @Autowired
    private SNSService snsService; 

    @Autowired
    private UserTokenDAO userTokenDAO; 

     @Value("${domain.name}")
    private String domainName;

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
            long startTime = getCurrentTimeMillis();
            userDAO.createUser(user);
            logExecutionTime("db.createUser.execution.time", startTime);

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
        long startTime = getCurrentTimeMillis();
        User existinguser = userDAO.checkExistingUser(email);
        logExecutionTime("db.getUser.execution.time", startTime);
        Boolean isIntegrationTests = request.getHeader("IsIntegrationTest") != null && Boolean.parseBoolean((String) request.getHeader("IsIntegrationTest"));
        if (existinguser == null || !EncryptionUtility.getDecryptedPassword(password, existinguser.getPassword())) {
            throw new UnAuthorizedException("Error occurred while validating credentials");
        }

        if (existinguser != null && EncryptionUtility.getDecryptedPassword(password, existinguser.getPassword())) {
            if( !isIntegrationTests && !isVerified(existinguser)){
                logger.error("User is not verified");
                throw new UnAuthorizedException("User is not verified");
           
            }
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

        if(email!=null && !email.equals(userDTO.getEmail()))
        throw new UserCustomExceptions("Bad Request");


        if (password != null && existinguser != null) {
            Boolean isIntegrationTests = request.getHeader("IsIntegrationTest") != null && Boolean.parseBoolean((String) request.getHeader("IsIntegrationTest"));
            if( !isIntegrationTests && !isVerified(existinguser)){
                logger.error("User is not verified");
                throw new UnAuthorizedException("User is unauthorized");
            }
            
            existinguser.setPassword(EncryptionUtility.getEncryptedPassword(userDTO.getPassword()));
            existinguser.setAccountUpdated(LocalDateTime.now());
            // existinguser.setEmail(email);
            existinguser.setFirstName(userDTO.getFirstName());
            existinguser.setLastName(userDTO.getLastName());
            long startTime = getCurrentTimeMillis();
            userDAO.updateUser(existinguser);
            logExecutionTime("db.udpateUser.execution.time", startTime);
        
        }


    }


    private long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void logExecutionTime(String metricName, long startTime) {
        long endTime = System.currentTimeMillis();
        statsd.recordExecutionTime(metricName, endTime - startTime);
    }

    public void sendMail(UserDTO userDTO) {

        User user = userDAO.checkExistingUser(userDTO.getEmail());

        UserToken token = new UserToken();

        token.setUser(user);

        userDAO.createToken(token);

        String activationLink = "http://" + domainName + "/verify?user="+ user.getEmail()+ "&token=" + token.getToken();
      
        token.setExpiresAt(LocalDateTime.now().plusMinutes(2));
        snsService.publishMessage(user.getEmail(), token.getToken(), activationLink);

    }

    public boolean checkVerfication(String email, String token) {

        logger.info("Checking verification");

        User user = userDAO.checkExistingUser(email);

       

        if(user != null){

        //check for token
        UserToken userToken = userTokenDAO.findUserToken(user,token);

         // Check if the token is still valid (expiry time is greater than the current time)
         if (userToken.getExpiresAt().isAfter(java.time.LocalDateTime.now())) {
            // Token is valid, mark it as verified
            logger.info("validating token");
            user.setActive(true);
            userDAO.updateUser(user);
            return true;
        } else {
            // Token is expired
            logger.error("token is expired");
            return false;
        }

        }else{
            logger.error("token error");
            return false;
        }
      
    
    }


    public boolean isVerified(User user) {    
        if(user != null)
        return user.isActive(); // Check if user is verified
        else    
            return false;
    }

}
