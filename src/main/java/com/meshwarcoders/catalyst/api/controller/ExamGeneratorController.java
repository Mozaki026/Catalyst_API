package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.request.GenerateExamRequest;
import com.meshwarcoders.catalyst.api.dto.response.ApiResponse;
import com.meshwarcoders.catalyst.api.dto.response.QuestionResponse;
import com.meshwarcoders.catalyst.api.service.ExamGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-generator")
@RequiredArgsConstructor
public class ExamGeneratorController {

    private final ExamGeneratorService examGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> generateExam(
            @RequestBody GenerateExamRequest request) {

        List<QuestionResponse> questions = examGeneratorService.generateQuestion(request);

        return ResponseEntity.ok(
                ApiResponse.<List<QuestionResponse>>builder()
                        .success(true)
                        .message("Exam generated successfully")
                        .data(questions)
                        .build()
        );
    }
}