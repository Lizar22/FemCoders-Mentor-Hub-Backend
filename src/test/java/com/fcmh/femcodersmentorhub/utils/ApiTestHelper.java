package com.fcmh.femcodersmentorhub.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApiTestHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ApiTestHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public <T> ResultActions performRequest(MockHttpServletRequestBuilder requestBuilder, T requestBody, String expectedMessage) throws Exception {
        String json = objectMapper.writeValueAsString(requestBody);

        return mockMvc.perform(requestBuilder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.data").exists());
    }

    public <T> void performErrorRequest(MockHttpServletRequestBuilder requestBuilder,
                                        T requestBody,
                                        String expectedErrorCode,
                                        int expectedStatus,
                                        String expectedMessageContains) throws Exception {
        String json = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(requestBuilder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(expectedStatus))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(expectedErrorCode))
                .andExpect(jsonPath("$.message").value(containsString(expectedMessageContains)))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }
}
