package com.accounting.platform.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.accounting.platform.JavaServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = JavaServiceApplication.class)
@AutoConfigureMockMvc
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void register_WithInvalidEmail_Returns400AndValidationError() throws Exception {
    String badRequest =
        """
                {
                  "email": "not-an-email",
                  "password": "123",
                  "fullName": ""
                }
                """;

    mockMvc
        .perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(badRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.fields.email").exists())
        .andExpect(jsonPath("$.fields.password").exists());
  }
}
