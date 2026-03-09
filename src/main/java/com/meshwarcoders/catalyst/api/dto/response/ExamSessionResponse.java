package com.meshwarcoders.catalyst.api.dto.response;
import com.meshwarcoders.catalyst.api.dto.response.QuestionResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ExamSessionResponse {
    private String sessionId;
    private String userRequest;
    private List<QuestionResponse> questions;
    private int totalQuestions;
    private LocalDateTime createdAt;
}