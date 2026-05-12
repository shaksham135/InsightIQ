package com.insightiq.insightiq.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class GptServiceTest {

    private RestTemplate restTemplate;
    private GptService gptService;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        gptService = new GptService(restTemplate);
        
        // Set @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(gptService, "apiKey", "dummy-key");
        ReflectionTestUtils.setField(gptService, "model", "dummy-model");
    }

    @Test
    void generateSummary_ReturnsMockedResponse() {
        // Arrange
        Map<String, Object> responseBody = new HashMap<>();
        List<Map<String, Object>> choices = new ArrayList<>();
        Map<String, Object> choice = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("content", "Mocked Summary");
        choice.put("message", message);
        choices.add(choice);
        responseBody.put("choices", choices);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq("https://api.groq.com/openai/v1/chat/completions"),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        String result = gptService.generateSummary("Test content");

        // Assert
        assertEquals("Mocked Summary", result);
    }

    @Test
    void answerQuestion_ReturnsMockedResponse() {
        // Arrange
        Map<String, Object> responseBody = new HashMap<>();
        List<Map<String, Object>> choices = new ArrayList<>();
        Map<String, Object> choice = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("content", "Mocked Answer");
        choice.put("message", message);
        choices.add(choice);
        responseBody.put("choices", choices);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq("https://api.groq.com/openai/v1/chat/completions"),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        String result = gptService.answerQuestion("Test context", "Test question");

        // Assert
        assertEquals("Mocked Answer", result);
    }

    @Test
    void generateSummary_Exception() {
        // Arrange
        when(restTemplate.postForEntity(
                eq("https://api.groq.com/openai/v1/chat/completions"),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("API down"));

        // Act
        String result = gptService.generateSummary("Test content");

        // Assert
        assertEquals("Error calling OpenAI: API down", result);
    }

    @Test
    void generateSummary_NoResponse() {
        // Arrange
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq("https://api.groq.com/openai/v1/chat/completions"),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        String result = gptService.generateSummary("Test content");

        // Assert
        assertEquals("No response from AI.", result);
    }
}
