package com.meshwarcoders.catalyst.api.dto.response;

import com.meshwarcoders.catalyst.api.model.common.ExamStatus;
import com.meshwarcoders.catalyst.api.model.common.ExamType;

public record StudentExamSummaryDto(
        Long id,
        Long lessonId,
        String examName,
        Integer maxGrade,
        String examDateTime,
        String closingDate,
        Integer durationMinutes,
        ExamType examType,
        Boolean completed,
        Integer studentGrade,
        ExamStatus status) {
}
