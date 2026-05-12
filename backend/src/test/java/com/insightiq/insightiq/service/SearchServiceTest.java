package com.insightiq.insightiq.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    private final SearchService searchService = new SearchService();

    @Test
    void getRelevantChunks_NullContent_ReturnsEmptyList() {
        List<String> chunks = searchService.getRelevantChunks(null, "query");
        assertTrue(chunks.isEmpty());
    }

    @Test
    void getRelevantChunks_EmptyContent_ReturnsEmptyList() {
        List<String> chunks = searchService.getRelevantChunks("", "query");
        assertTrue(chunks.isEmpty());
    }

    @Test
    void getRelevantChunks_WithMatches_ReturnsMatchedChunks() {
        String content = "Java is a programming language.\n\nPython is popular for AI.\n\nJavaScript is for web.";
        String query = "AI Python";
        
        List<String> chunks = searchService.getRelevantChunks(content, query);
        
        assertEquals(1, chunks.size());
        assertEquals("Python is popular for AI.", chunks.get(0));
    }

    @Test
    void getRelevantChunks_NoMatches_ReturnsFallbackChunks() {
        String content = "First chunk.\n\nSecond chunk.\n\nThird chunk.\n\nFourth chunk.";
        String query = "notfound";
        
        List<String> chunks = searchService.getRelevantChunks(content, query);
        
        assertEquals(3, chunks.size());
        assertEquals("First chunk.", chunks.get(0));
        assertEquals("Second chunk.", chunks.get(1));
        assertEquals("Third chunk.", chunks.get(2));
    }

    @Test
    void getRelevantChunks_FallsBackToSingleNewline() {
        String content = "Apple\nBanana\nOrange";
        String query = "Banana";
        
        List<String> chunks = searchService.getRelevantChunks(content, query);
        
        assertEquals(1, chunks.size());
        assertEquals("Banana", chunks.get(0));
    }
}
