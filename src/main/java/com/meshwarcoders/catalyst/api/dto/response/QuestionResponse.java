package com.meshwarcoders.catalyst.api.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class QuestionResponse {
    private String question;
    private List<String> options;
    private List<Integer> correctOptions;
    private String questionType;
    private String textAnswer;
}