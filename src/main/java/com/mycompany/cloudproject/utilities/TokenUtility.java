package com.mycompany.cloudproject.utilities;

import com.mycompany.cloudproject.model.User;
import com.mycompany.cloudproject.model.UserToken;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static UserToken generateBasicAuthToken(User user) {
        String credentials = user.getEmail() + ":" + user.getPassword();
        UserToken token = new UserToken();
        token.setToken(Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
        token.setUsername(user.getEmail());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setCreatedAt(LocalDateTime.now());
        return token;
    }

}
