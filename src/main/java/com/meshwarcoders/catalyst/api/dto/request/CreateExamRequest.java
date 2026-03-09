package com.meshwarcoders.catalyst.api.dto.request;

import com.meshwarcoders.catalyst.api.model.common.ExamType;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateExamRequest(@NotBlank(message = "examName is required") String examName, Integer maxGrade,
                                String examDateTime, String closingDate, Integer durationMinutes, Integer defaultPoints,
                                List<QuestionRequest> questions, ExamType examType ) {
}