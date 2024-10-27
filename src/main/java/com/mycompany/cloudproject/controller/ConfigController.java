package com.mycompany.cloudproject.controller;


import com.mycompany.cloudproject.service.ConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class.getName());

    @Autowired
    ConfigService service;

    @GetMapping("/healthz")
    @ResponseBody
    public ResponseEntity<String> getConfig(@RequestBody(required = false) String payload, HttpServletResponse response, HttpServletRequest request) {

        setResponseHeaders(response);
        if (payload != null && !payload.isEmpty()) {
            logger.info("Payload is not supported. Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (request.getContentLength() > 0) {
            logger.info("Payload is not supported. Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (request.getHeader("Authorization") != null) {
            logger.info("Authorization is not supported. Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (!request.getParameterMap().isEmpty()) {
            logger.info("Unexpected parameters detected: " + request.getParameterMap());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (service.getConfig()) {
            logger.info("Getting success response");
            return ResponseEntity.ok().build();
        }
        logger.info("Service is unavailable now. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    public void setResponseHeaders(HttpServletResponse response) {
        logger.info("setting response headers");
        response.setHeader("cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Content-Type-Options", "nosniff");
        logger.info("Finished setting response headers");
    }

    @RequestMapping(path = "/healthz", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.TRACE})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void unSupportedMethods(HttpServletResponse response) {
        logger.error("Unsupported HTTP method");
        setResponseHeaders(response);
    }

  


}
