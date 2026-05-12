package com.insightiq.insightiq.controller;

import com.insightiq.insightiq.dto.ApiResponse;
import com.insightiq.insightiq.dto.ChatRequest;
import com.insightiq.insightiq.entity.ChatHistory;
import com.insightiq.insightiq.entity.Transcript;
import com.insightiq.insightiq.repository.ChatHistoryRepository;
import com.insightiq.insightiq.service.FileService;
import com.insightiq.insightiq.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.insightiq.insightiq.service.SearchService;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private FileService fileService;

    @Autowired
    private GptService gptService;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private SearchService searchService;

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<String>> askQuestion(@RequestBody ChatRequest request) {
        Optional<Transcript> transcript = fileService.getTranscript(request.getFileId());
        if (transcript.isPresent()) {
            List<String> relevantChunks = searchService.getRelevantChunks(transcript.get().getContent(), request.getQuestion());
            String context = String.join("\n\n", relevantChunks);

            String answer = gptService.answerQuestion(context, request.getQuestion());

            // Save to history
            ChatHistory history = new ChatHistory();
            history.setFile(transcript.get().getFile());
            history.setQuestion(request.getQuestion());
            history.setAnswer(answer);
            chatHistoryRepository.save(history);

            return ResponseEntity.ok(ApiResponse.success(answer));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Transcript not found for this file"));
    }
}
