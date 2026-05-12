package com.insightiq.insightiq.controller;

import com.insightiq.insightiq.dto.ApiResponse;
import com.insightiq.insightiq.entity.MediaFile;
import com.insightiq.insightiq.entity.Transcript;
import com.insightiq.insightiq.service.FileService;
import com.insightiq.insightiq.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private GptService gptService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<MediaFile>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        MediaFile mediaFile = fileService.uploadAndProcessFile(file);
        return ResponseEntity.ok(ApiResponse.success(mediaFile, "File uploaded and processed successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaFile>>> getAllFiles() {
        List<MediaFile> files = fileService.getAllFiles();
        return ResponseEntity.ok(ApiResponse.success(files));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaFile>> getFile(@PathVariable Long id) {
        Optional<MediaFile> file = fileService.getFile(id);
        return file.map(mediaFile -> ResponseEntity.ok(ApiResponse.success(mediaFile)))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error("File not found")));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ApiResponse<String>> getSummary(@PathVariable Long id) {
        Optional<Transcript> transcript = fileService.getTranscript(id);
        if (transcript.isPresent()) {
            Transcript t = transcript.get();
            if (t.getSummary() != null && !t.getSummary().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success(t.getSummary()));
            }
            
            // Fallback: generate if not found (for old files)
            String summary = gptService.generateSummary(t.getContent());
            t.setSummary(summary);
            fileService.saveTranscript(t); // Save it so we don't generate again
            return ResponseEntity.ok(ApiResponse.success(summary));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Transcript not found for this file"));
    }

    @GetMapping("/{id}/timestamp")
    public ResponseEntity<ApiResponse<String>> getTimestamp(@PathVariable Long id) {
        Optional<Transcript> transcript = fileService.getTranscript(id);
        if (transcript.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(transcript.get().getTimestamps()));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Transcript not found for this file"));
    }
}
