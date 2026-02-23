package com.meshwarcoders.catalyst.api.dto.response;

import java.util.List;

public record StudentExamAnswersDto(
        String studentName,
        Integer totalGrade,
        List<StudentAnswerDto> answers) {
}
