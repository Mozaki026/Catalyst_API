package com.meshwarcoders.catalyst.api.dto.request;

import lombok.Data;

@Data
public class GenerateExamRequest {
    private long examId;
    private String userMessage;
}