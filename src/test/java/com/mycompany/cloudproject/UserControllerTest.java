package com.mycompany.cloudproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.cloudproject.dto.UserDTO;
import com.mycompany.cloudproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private UserDTO userDTO;

    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
     

        String userJson = "{"
        + "\"first_name\": \"manali\","
        + "\"last_name\": \"rama\","
        + "\"password\": \"12378795\","
        + "\"email\": \"maal@example.com\""
        + "}";


        // Mock the HttpServletRequest
        httpServletRequest = Mockito.mock(HttpServletRequest.class);

        // Save the user to the database
        // Perform the POST request to create the user
        try {
            mockMvc.perform(post("/v1/user")
                    .header("IsIntegrationTest", "true") // Set IsIntegrationTest header
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson))
                    .andExpect(status().isCreated()); // Assuming the user is created successfully (201 Created)
        } catch (Exception e) {
            throw new RuntimeException("Error during user creation", e);
        }
    }

    @Test
    public void testGetUserDetails() throws Exception {
        String username = "maal@example.com"; // updated email
        String password = "12378795"; // actual password
        String base64Credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        mockMvc.perform(get("/v1/user/self")
                .header("Authorization", "Basic " + base64Credentials)
                .header("IsIntegrationTest", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("manali"));
    }

    @Test
    public void testUpdateUserDetails() throws Exception {
        String username = "maal@example.com"; // existing user's email
        String password = "12378795"; // actual password
        String base64Credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        // JSON content for the update request
        String updatedUserJson = "{"
                + "\"first_name\": \"updated\","
                + "\"last_name\": \"updated\","
                + "\"email\": \"maal@example.com\","
                + "\"password\": \"12378795\""
                + "}";

        // Perform the update request
        mockMvc.perform(put("/v1/user/self")
                .header("Authorization", "Basic " + base64Credentials)
                .header("IsIntegrationTest", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andDo(result -> {
                    // Log the error response if the status is 400
                    if (result.getResponse().getStatus() == 400) {
                        System.out.println("Error response: " + result.getResponse().getContentAsString());
                    }
                })
                .andExpect(status().isNoContent());

        // Verify that the user's details were updated
        mockMvc.perform(get("/v1/user/self")
                .header("Authorization", "Basic " + base64Credentials)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("updated"))
                .andExpect(jsonPath("$.last_name").value("updated"));
    }

}
