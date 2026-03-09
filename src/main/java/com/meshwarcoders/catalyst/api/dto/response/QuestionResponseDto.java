package com.meshwarcoders.catalyst.api.dto.response;

import lombok.Data;

@Data
public class QuestionResponseDto {

    private Long id;
    private String questionText;
    private String correctAnswer;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    // بيانات الدرس (مختصرة)
    private Long lessonId;
    private String lessonSubject;
}