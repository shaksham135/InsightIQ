package com.insightiq.insightiq.controller;

import com.insightiq.insightiq.dto.ApiResponse;
import com.insightiq.insightiq.entity.MediaFile;
import com.insightiq.insightiq.entity.Transcript;
import com.insightiq.insightiq.service.FileService;
import com.insightiq.insightiq.service.GptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "CLOUDINARY_CLOUD_NAME=dummy",
    "CLOUDINARY_API_KEY=dummy",
    "CLOUDINARY_API_SECRET=dummy",
    "GROQ_API_KEY=dummy"
})
class FileControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private GptService gptService;

    @Test
    void uploadFile_Success() throws Exception {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(1L);
        mediaFile.setFileName("test.pdf");

        when(fileService.uploadAndProcessFile(any())).thenReturn(mediaFile);

        String url = "http://localhost:" + port + "/api/files/upload";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource("dummy pdf".getBytes()) {
            @Override
            public String getFilename() {
                return "test.pdf";
            }
        };
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url, requestEntity, ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void uploadFile_Failure() throws Exception {
        when(fileService.uploadAndProcessFile(any())).thenThrow(new java.io.IOException("Upload failed"));

        String url = "http://localhost:" + port + "/api/files/upload";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileResource = new ByteArrayResource("dummy pdf".getBytes()) {
            @Override
            public String getFilename() {
                return "test.pdf";
            }
        };
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        org.springframework.web.client.HttpServerErrorException exception = 
            org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpServerErrorException.class, () -> {
                restTemplate.postForEntity(url, requestEntity, ApiResponse.class);
            });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    void getAllFiles_Success() {
        MediaFile file1 = new MediaFile();
        file1.setId(1L);
        MediaFile file2 = new MediaFile();
        file2.setId(2L);

        when(fileService.getAllFiles()).thenReturn(List.of(file1, file2));

        String url = "http://localhost:" + port + "/api/files";
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void getAllFiles_Error() {
        when(fileService.getAllFiles()).thenThrow(new RuntimeException("Database down"));

        String url = "http://localhost:" + port + "/api/files";
        
        org.springframework.web.client.HttpServerErrorException exception = 
            org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpServerErrorException.class, () -> {
                restTemplate.getForEntity(url, ApiResponse.class);
            });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    void getAllFiles_MaxSizeError() {
        when(fileService.getAllFiles()).thenThrow(new org.springframework.web.multipart.MaxUploadSizeExceededException(5000L));

        String url = "http://localhost:" + port + "/api/files";
        
        org.springframework.web.client.HttpClientErrorException exception = 
            org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpClientErrorException.class, () -> {
                restTemplate.getForEntity(url, ApiResponse.class);
            });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void getFile_Success() {
        MediaFile file = new MediaFile();
        file.setId(1L);

        when(fileService.getFile(1L)).thenReturn(Optional.of(file));

        String url = "http://localhost:" + port + "/api/files/1";
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void getFile_NotFound() {
        when(fileService.getFile(1L)).thenReturn(Optional.empty());

        String url = "http://localhost:" + port + "/api/files/1";
        
        org.springframework.web.client.HttpClientErrorException exception = 
            org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpClientErrorException.class, () -> {
                restTemplate.getForEntity(url, ApiResponse.class);
            });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getSummary_Success() {
        Transcript transcript = new Transcript();
        transcript.setSummary("Mocked Summary");

        when(fileService.getTranscript(1L)).thenReturn(Optional.of(transcript));

        String url = "http://localhost:" + port + "/api/files/1/summary";
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void getTimestamp_Success() {
        Transcript transcript = new Transcript();
        transcript.setTimestamps("Mocked Timestamps");

        when(fileService.getTranscript(1L)).thenReturn(Optional.of(transcript));

        String url = "http://localhost:" + port + "/api/files/1/timestamp";
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
    }

    @Test
    void getSummary_Fallback() {
        Transcript transcript = new Transcript();
        transcript.setContent("Full content");
        transcript.setSummary(null);

        when(fileService.getTranscript(1L)).thenReturn(Optional.of(transcript));
        when(gptService.generateSummary("Full content")).thenReturn("Generated Summary");

        String url = "http://localhost:" + port + "/api/files/1/summary";
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals("Generated Summary", response.getBody().getData());
    }

    @Test
    void getTimestamp_NotFound() {
        when(fileService.getTranscript(1L)).thenReturn(Optional.empty());

        String url = "http://localhost:" + port + "/api/files/1/timestamp";
        
        org.springframework.web.client.HttpClientErrorException exception = 
            org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpClientErrorException.class, () -> {
                restTemplate.getForEntity(url, ApiResponse.class);
            });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
