package com.mycompany.cloudproject.utilities;

import jakarta.servlet.http.HttpServletRequest;

public class RequestCheckUtility {

    public static Boolean checkRequestBody(HttpServletRequest request) {

          boolean hasContent = request.getContentLength() > 0;
          boolean containsParameters = request.getParameterMap().isEmpty();

          return hasContent && containsParameters;
        }

    public static Boolean checkValidBasicAuthHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader != null && authHeader.startsWith("Basic ");
    }

    public static Boolean checkForParameterMap(HttpServletRequest request) {
        return request.getParameterMap().isEmpty();
    }



}
