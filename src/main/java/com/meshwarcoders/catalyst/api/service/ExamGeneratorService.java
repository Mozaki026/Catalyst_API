package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.request.GenerateExamRequest;
import com.meshwarcoders.catalyst.api.dto.response.QuestionResponse;
import com.meshwarcoders.catalyst.api.repository.QuestionRepository;
import com.meshwarcoders.catalyst.api.repository.ExamRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamGeneratorService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final OpenRouterClientService openRouterClientService;
    private final ExamRepository examRepository;
    private final QuestionRepository examQuestionRepository;


    public List<QuestionResponse> generateQuestion(GenerateExamRequest request) {
        String prompt = buildPrompt(request);
        String aiResponse = openRouterClientService.callAI(prompt,request.getUserMessage());
        List<QuestionResponse> questions = parseQuestions(aiResponse);
        System.out.println("aiResponse");
        System.out.println(aiResponse);

        return questions;
    }

    private String buildPrompt(GenerateExamRequest request) {
        return String.format("""
            You are an exam generator. generate exactly the number of questions the user want.
            
            Rules:
            - For MCQ: provide 4 options (A, B, C, D) and specify the correct option indexs 
            - For TRUE_FALSE: options should be ["True", "False"] and 0 for true and 1 for false
            - For WRITING: Provide text answer
            - Return ONLY a valid JSON array, no extra text
            
            JSON Format:
            [
              {
                "question": "Question text here?",
                "options": ["A. option1", "B. option2", "C. option3", "D. option4"],
                "correctOptions": [0],
                "questionType": "the type of question MCQ or WRITING",
                "textAnswer": "the answer of the written question"
              }
            ]
            """
        );
    }


    public static List<QuestionResponse> parseQuestions(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<QuestionResponse>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

}