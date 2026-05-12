package com.insightiq.insightiq.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class WhisperServiceTest {

    private RestTemplate restTemplate;
    private WhisperService whisperService;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        whisperService = new WhisperService(restTemplate);
        
        // Set @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(whisperService, "apiKey", "dummy-key");
    }

    @Test
    void transcribe_ReturnsMockedResponse() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mp3", "test data".getBytes());
        String mockResponseBody = "{\"text\": \"Mocked transcription\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq("https://api.groq.com/openai/v1/audio/transcriptions"),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        // Act
        String result = whisperService.transcribe(file);

        // Assert
        assertEquals(mockResponseBody, result);
    }
}
