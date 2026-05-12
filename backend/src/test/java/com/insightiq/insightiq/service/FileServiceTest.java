package com.insightiq.insightiq.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insightiq.insightiq.entity.MediaFile;
import com.insightiq.insightiq.entity.Transcript;
import com.insightiq.insightiq.repository.MediaFileRepository;
import com.insightiq.insightiq.repository.TranscriptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FileServiceTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private PdfExtractionService pdfExtractionService;

    @Mock
    private WhisperService whisperService;

    @Mock
    private GptService gptService;

    @Mock
    private MediaFileRepository fileRepository;

    @Mock
    private TranscriptRepository transcriptRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadAndProcessFile_Pdf_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy pdf".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://cloudinary.com/test.pdf");

        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(1L);
        mediaFile.setFileName("test.pdf");

        when(cloudinaryService.upload(file)).thenReturn(uploadResult);
        when(fileRepository.save(any(MediaFile.class))).thenReturn(mediaFile);
        when(pdfExtractionService.extractText(file)).thenReturn("Extracted PDF Text");
        when(gptService.generateSummary("Extracted PDF Text")).thenReturn("Mocked Summary");

        // Act
        MediaFile result = fileService.uploadAndProcessFile(file);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void uploadAndProcessFile_Audio_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mp3", "dummy audio".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://cloudinary.com/test.mp3");

        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(2L);
        mediaFile.setFileName("test.mp3");

        JsonNode jsonNode = Mockito.mock(JsonNode.class);
        JsonNode textNode = Mockito.mock(JsonNode.class);

        when(cloudinaryService.upload(file)).thenReturn(uploadResult);
        when(fileRepository.save(any(MediaFile.class))).thenReturn(mediaFile);
        when(whisperService.transcribe(file)).thenReturn("{\"text\": \"Transcribed Audio Text\"}");
        when(objectMapper.readTree("{\"text\": \"Transcribed Audio Text\"}")).thenReturn(jsonNode);
        when(jsonNode.path("text")).thenReturn(textNode);
        when(textNode.asText()).thenReturn("Transcribed Audio Text");
        when(gptService.generateSummary("Transcribed Audio Text")).thenReturn("Mocked Summary");

        // Act
        MediaFile result = fileService.uploadAndProcessFile(file);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void uploadAndProcessFile_Audio_JsonParsingError() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mp3", "dummy audio".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://cloudinary.com/test.mp3");

        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(2L);
        mediaFile.setFileName("test.mp3");

        when(cloudinaryService.upload(file)).thenReturn(uploadResult);
        when(fileRepository.save(any(MediaFile.class))).thenReturn(mediaFile);
        when(whisperService.transcribe(file)).thenReturn("invalid json");
        when(objectMapper.readTree("invalid json")).thenThrow(new RuntimeException("Parsing failed"));
        when(gptService.generateSummary("invalid json")).thenReturn("Mocked Summary");

        // Act
        MediaFile result = fileService.uploadAndProcessFile(file);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void uploadAndProcessFile_GptError() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy pdf".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://cloudinary.com/test.pdf");

        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(1L);
        mediaFile.setFileName("test.pdf");

        when(cloudinaryService.upload(file)).thenReturn(uploadResult);
        when(fileRepository.save(any(MediaFile.class))).thenReturn(mediaFile);
        when(pdfExtractionService.extractText(file)).thenReturn("Extracted PDF Text");
        when(gptService.generateSummary("Extracted PDF Text")).thenThrow(new RuntimeException("GPT down"));

        // Act
        MediaFile result = fileService.uploadAndProcessFile(file);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void uploadAndProcessFile_EmptyContent() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy pdf".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://cloudinary.com/test.pdf");

        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(1L);
        mediaFile.setFileName("test.pdf");

        when(cloudinaryService.upload(file)).thenReturn(uploadResult);
        when(fileRepository.save(any(MediaFile.class))).thenReturn(mediaFile);
        when(pdfExtractionService.extractText(file)).thenReturn(""); // Empty content!

        // Act
        MediaFile result = fileService.uploadAndProcessFile(file);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void uploadAndProcessFile_UnknownType() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "dummy text".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://cloudinary.com/test.txt");

        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(3L);
        mediaFile.setFileName("test.txt");

        when(cloudinaryService.upload(file)).thenReturn(uploadResult);
        when(fileRepository.save(any(MediaFile.class))).thenReturn(mediaFile);

        // Act
        MediaFile result = fileService.uploadAndProcessFile(file);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test
    void uploadAndProcessFile_Video_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "dummy video".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "http://cloudinary.com/test.mp4");

        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(4L);
        mediaFile.setFileName("test.mp4");

        JsonNode jsonNode = Mockito.mock(JsonNode.class);
        JsonNode textNode = Mockito.mock(JsonNode.class);

        when(cloudinaryService.upload(file)).thenReturn(uploadResult);
        when(fileRepository.save(any(MediaFile.class))).thenReturn(mediaFile);
        when(whisperService.transcribe(file)).thenReturn("{\"text\": \"Transcribed Video Text\"}");
        when(objectMapper.readTree("{\"text\": \"Transcribed Video Text\"}")).thenReturn(jsonNode);
        when(jsonNode.path("text")).thenReturn(textNode);
        when(textNode.asText()).thenReturn("Transcribed Video Text");
        when(gptService.generateSummary("Transcribed Video Text")).thenReturn("Mocked Summary");

        // Act
        MediaFile result = fileService.uploadAndProcessFile(file);

        // Assert
        assertNotNull(result);
        assertEquals(4L, result.getId());
    }
}
