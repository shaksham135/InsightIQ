package com.insightiq.insightiq.controller;

import com.insightiq.insightiq.dto.ApiResponse;
import com.insightiq.insightiq.dto.ChatRequest;
import com.insightiq.insightiq.entity.Transcript;
import com.insightiq.insightiq.repository.ChatHistoryRepository;
import com.insightiq.insightiq.service.FileService;
import com.insightiq.insightiq.service.GptService;
import com.insightiq.insightiq.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "CLOUDINARY_CLOUD_NAME=dummy",
    "CLOUDINARY_API_KEY=dummy",
    "CLOUDINARY_API_SECRET=dummy",
    "GROQ_API_KEY=dummy"
})
class ChatControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private GptService gptService;

    @MockitoBean
    private ChatHistoryRepository chatHistoryRepository;

    @MockitoBean
    private SearchService searchService;

    @Test
    void askQuestion_Success() {
        ChatRequest request = new ChatRequest();
        request.setFileId(1L);
        request.setQuestion("What is Java?");

        Transcript transcript = new Transcript();
        transcript.setContent("Java is a language.");

        when(fileService.getTranscript(1L)).thenReturn(Optional.of(transcript));
        when(searchService.getRelevantChunks(any(), eq("What is Java?"))).thenReturn(List.of("Java is a language."));
        when(gptService.answerQuestion(any(), eq("What is Java?"))).thenReturn("It is a language.");

        String url = "http://localhost:" + port + "/api/chat/ask";
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url, request, ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("It is a language.", response.getBody().getData());
    }

    @Test
    void askQuestion_NotFound() {
        ChatRequest request = new ChatRequest();
        request.setFileId(1L);
        request.setQuestion("What is Java?");

        when(fileService.getTranscript(1L)).thenReturn(Optional.empty());

        String url = "http://localhost:" + port + "/api/chat/ask";
        
        org.springframework.web.client.HttpClientErrorException exception = 
            org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpClientErrorException.class, () -> {
                restTemplate.postForEntity(url, request, ApiResponse.class);
            });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
