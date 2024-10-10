package com.mycompany.cloudproject;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@SpringBootTest
@AutoConfigureMockMvc
public class UnitTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testDeleteUserEndpointNotFound() throws Exception {


        mockMvc.perform(get("/v1/user/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect 404 Not Found if the endpoint doesn't exist
    }

    @Test
    public void testDeleteUserEndpointMethodNotAllowed() throws Exception {

        mockMvc.perform(delete("/v1/user/self")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed()); // Expect 405 Method Not Allowed
    }
}
