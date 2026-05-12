package com.insightiq.insightiq.service;

import com.insightiq.insightiq.entity.MediaFile;
import com.insightiq.insightiq.entity.Transcript;
import com.insightiq.insightiq.repository.MediaFileRepository;
import com.insightiq.insightiq.repository.TranscriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PdfExtractionService pdfExtractionService;

    @Autowired
    private WhisperService whisperService;

    @Autowired
    private GptService gptService;

    @Autowired
    private MediaFileRepository fileRepository;

    @Autowired
    private TranscriptRepository transcriptRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public MediaFile uploadAndProcessFile(MultipartFile file) throws IOException {
        // 1. Upload to Cloudinary
        Map uploadResult = cloudinaryService.upload(file);
        String url = (String) uploadResult.get("secure_url");
        String fileType = file.getContentType();

        // 2. Save MediaFile entity
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileName(file.getOriginalFilename());
        mediaFile.setFileType(fileType);
        mediaFile.setFilePath(url);
        mediaFile = fileRepository.save(mediaFile);

        // 3. Process content based on type
        String content = "";
        String timestamps = "";

        if (fileType != null && fileType.startsWith("application/pdf")) {
            content = pdfExtractionService.extractText(file);
        } else if (fileType != null && (fileType.startsWith("audio/") || fileType.startsWith("video/"))) {
            String whisperResponse = whisperService.transcribe(file);
            timestamps = whisperResponse; // Keep full JSON for timestamps
            
            try {
                JsonNode root = objectMapper.readTree(whisperResponse);
                content = root.path("text").asText();
            } catch (Exception e) {
                content = whisperResponse; // Fallback to raw JSON if parsing fails
            }
        }

        // 4. Save Transcript
        Transcript transcript = new Transcript();
        transcript.setFile(mediaFile);
        transcript.setContent(content);
        transcript.setTimestamps(timestamps);
        
        // Generate and store summary
        if (content != null && !content.isEmpty()) {
            try {
                String summary = gptService.generateSummary(content);
                transcript.setSummary(summary);
            } catch (Exception e) {
                // Fallback: don't fail upload if summary fails
                transcript.setSummary("Failed to generate summary.");
            }
        }
        
        transcriptRepository.save(transcript);

        return mediaFile;
    }

    public Optional<MediaFile> getFile(Long id) {
        return fileRepository.findById(id);
    }

    public Optional<Transcript> getTranscript(Long fileId) {
        return transcriptRepository.findByFileId(fileId);
    }

    public List<MediaFile> getAllFiles() {
        return fileRepository.findAll();
    }

    public void saveTranscript(Transcript transcript) {
        transcriptRepository.save(transcript);
    }
}
