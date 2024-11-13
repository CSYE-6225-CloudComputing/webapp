package com.mycompany.cloudproject.utilities;

import com.mycompany.cloudproject.model.User;
import com.mycompany.cloudproject.model.UserToken;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TokenUtility {

    public static Map<String,String> getCredentials(String token) {
        Map<String, String> map = new HashMap<>();

        if (token != null && token.startsWith("Basic ")) {
            token = token.substring(6).trim();
        }
       // System.out.println(token);
        String credentials = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        String[] values = credentials.split(":", 2);

        map.put("email", values[0]);
        map.put("password", values[1]);
        return map;
    }

   

}
