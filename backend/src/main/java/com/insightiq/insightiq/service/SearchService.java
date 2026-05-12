package com.insightiq.insightiq.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    public List<String> getRelevantChunks(String content, String query) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }

        // Split by double newline (paragraphs) as a simple chunking strategy
        String[] chunks = content.split("\n\n");
        if (chunks.length == 1) {
            // Fallback to single newline if no paragraphs found
            chunks = content.split("\n");
        }

        String[] keywords = query.toLowerCase().split("\\s+");

        List<String> matchedChunks = Arrays.stream(chunks)
                .filter(chunk -> {
                    String lowerChunk = chunk.toLowerCase();
                    return Arrays.stream(keywords).anyMatch(lowerChunk::contains);
                })
                .limit(5) // Limit to top 5 chunks to avoid token limits
                .collect(Collectors.toList());

        if (matchedChunks.isEmpty()) {
            // Fallback: return first few chunks if no matches found
            return Arrays.stream(chunks).limit(3).collect(Collectors.toList());
        }

        return matchedChunks;
    }
}
