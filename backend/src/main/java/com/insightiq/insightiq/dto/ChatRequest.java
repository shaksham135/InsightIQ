package com.insightiq.insightiq.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private Long fileId;
    private String question;
}
