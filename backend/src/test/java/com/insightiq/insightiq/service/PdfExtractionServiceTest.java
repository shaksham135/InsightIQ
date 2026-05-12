package com.insightiq.insightiq.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PdfExtractionServiceTest {

    private final PdfExtractionService pdfExtractionService = new PdfExtractionService();

    @Test
    void extractText_InvalidPdf_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "invalid pdf data".getBytes());
        
        assertThrows(IOException.class, () -> {
            pdfExtractionService.extractText(file);
        });
    }

    @Test
    void extractText_Success() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            byte[] pdfBytes = baos.toByteArray();
            
            MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", pdfBytes);
            
            String extractedText = pdfExtractionService.extractText(file);
            assertNotNull(extractedText);
        }
    }
}
