package com.insightiq.insightiq.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class PdfExtractionService {

    public String extractText(MultipartFile file) throws IOException {
        // Create a temporary file to avoid loading the whole PDF into memory
        File tempFile = File.createTempFile("upload-", ".pdf");
        file.transferTo(tempFile);

        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(tempFile))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } finally {
            // Ensure the temp file is deleted
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
