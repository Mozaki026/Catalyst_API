package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.request.AnswerRequest;
import com.meshwarcoders.catalyst.api.dto.response.*;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.service.ExamService;
import com.meshwarcoders.catalyst.api.service.LessonService;
import com.meshwarcoders.catalyst.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ExamService examService;

    @Autowired
    private LessonService lessonService;

    // ================== JOIN REQUEST (STUDENT) ==================
    @PostMapping("/lesson/{lessonId}/join-request")
    public ResponseEntity<ApiResponse> createJoinRequest(@PathVariable Long lessonId,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        JoinStudentDto dto = studentService.createJoinRequest(lessonId, email);

        return ResponseEntity.ok(new ApiResponse(true,
                "Join request created successfully!", dto));
    }

    // ================== EXAMS (STUDENT) ==================
    @GetMapping("/lesson/{lessonId}/exams")
    public ResponseEntity<ApiResponse> getExamsForLesson(@PathVariable Long lessonId,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        List<StudentExamSummaryDto> exams = examService.getExamsForLessonAsStudent(email, lessonId);
        return ResponseEntity.ok(new ApiResponse(true,
                "Exams fetched successfully!", exams));
    }

    @GetMapping("/lesson/my")
    public ResponseEntity<ApiResponse> getAllStudentLessons(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        var lessons = lessonService.getStudentLessons(email);
        return ResponseEntity.ok(new ApiResponse(true,
                "Student lessons fetched successfully!", lessons));
    }

    @GetMapping("/lesson/all")
    public ResponseEntity<ApiResponse> getAllLessons(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        var lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(new ApiResponse(true,
                "All lessons fetched successfully!", lessons));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse> getStudentLessonWithLessonID(@PathVariable Long lessonId,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        var lesson = lessonService.getStudentLessonDetails(lessonId, email);
        return ResponseEntity.ok(new ApiResponse(true,
                "Lesson details fetched successfully!", lesson));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<ApiResponse> getExamWithExamID(@PathVariable Long examId,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        StudentExamDetailsDto exam = examService.getExamByIdAsStudent(examId, email);
        return ResponseEntity.ok(new ApiResponse(true,
                "Exam details fetched successfully!", exam));
    }

    @PostMapping("/exam/{examId}/submit")
    public ResponseEntity<ApiResponse> submitExam(
            @PathVariable Long examId,
            @RequestBody List<AnswerRequest> request,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        examService.submitExam(email, examId, request);
        return ResponseEntity.ok(new ApiResponse(true,
                "Exam submitted successfully!", null));
    }
}
