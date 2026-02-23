package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.request.*;
import com.meshwarcoders.catalyst.api.dto.response.*;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.service.ExamService;
import com.meshwarcoders.catalyst.api.service.LessonService;
import com.meshwarcoders.catalyst.api.service.TeacherService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*")
public class TeacherController {

        private static final Logger log = LoggerFactory.getLogger(TeacherController.class);

        @Autowired
        private TeacherService teacherService;

        @Autowired
        private ExamService examService;

        @Autowired
        private LessonService lessonService;

        @Autowired
        private TeacherRepository teacherRepository;

        // ================== GET ALL TEACHERS ==================
        @GetMapping("/all")
        public ResponseEntity<ApiResponse> getAllTeachers() {
                var teachers = teacherRepository.findAll().stream()
                                .map(t -> new TeacherResponse(
                                                t.getId(),
                                                t.getFullName(),
                                                t.getEmail(),
                                                t.getEmailConfirmed(),
                                                t.getCreatedAt()))
                                .toList();

                return ResponseEntity.ok(
                                new ApiResponse(true, "Teachers fetched successfully!", teachers));
        }

        // ================== GET TEACHER BY ID ==================

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse> getTeacherById(@PathVariable Long id) {
                var teacher = teacherRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                TeacherResponse dto = new TeacherResponse(
                                teacher.getId(),
                                teacher.getFullName(),
                                teacher.getEmail(),
                                teacher.getEmailConfirmed(),
                                teacher.getCreatedAt());

                return ResponseEntity.ok(
                                new ApiResponse(true, "Teacher fetched successfully!", dto));
        }

        // ================== JOIN REQUESTS (TEACHER) ==================

        @GetMapping("/lesson/{lessonId}/join-requests")
        public ResponseEntity<ApiResponse> getPendingJoinRequests(@PathVariable Long lessonId,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                var list = teacherService.getPendingJoinRequests(teacher.getId(), lessonId);
                return ResponseEntity.ok(new ApiResponse(true,
                                "Pending join requests fetched successfully!", list));
        }

        @PostMapping("/lesson/{lessonId}/join-requests/approve")
        public ResponseEntity<ApiResponse> approveJoinRequests(@PathVariable Long lessonId,
                        @Valid @RequestBody JoinRequest request,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                var result = teacherService.approveJoinRequests(teacher.getId(), lessonId, request.studentLessonIds());
                return ResponseEntity.ok(new ApiResponse(true,
                                "Join requests approved successfully.", result));
        }

        @PostMapping("/lesson/{lessonId}/join-requests/reject")
        public ResponseEntity<ApiResponse> rejectJoinRequests(@PathVariable Long lessonId,
                        @Valid @RequestBody JoinRequest request,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                var result = teacherService.rejectJoinRequests(teacher.getId(), lessonId, request.studentLessonIds());
                return ResponseEntity.ok(new ApiResponse(true,
                                "Join requests rejected successfully.", result));
        }

        // ================== EXAMS (TEACHER) ==================

        @PostMapping("/lesson/{lessonId}/exams")
        public ResponseEntity<ApiResponse> createExam(@PathVariable Long lessonId,
                        @Valid @RequestBody CreateExamRequest request,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                ExamSummaryDto exam = examService.createExam(teacher.getId(), lessonId, request);
                return ResponseEntity.ok(new ApiResponse(true,
                                "Exam created successfully!", exam));
        }

        @GetMapping("/lesson/{lessonId}/exams")
        public ResponseEntity<ApiResponse> getExamsForLesson(@PathVariable Long lessonId,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                var exams = examService.getExamsForLessonAsTeacher(teacher.getId(), lessonId);
                return ResponseEntity.ok(new ApiResponse(true,
                                "Exams fetched successfully!", exams));
        }

        @PostMapping("/lesson")
        public ResponseEntity<ApiResponse> createLesson(@Valid @RequestBody CreateLessonRequest request,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                LessonSummaryDto data = lessonService.createLesson(teacher.getId(), request);

                return ResponseEntity.ok(new ApiResponse(true, "Lesson created successfully!", data));

        }

        @GetMapping("/lesson/my")
        public ResponseEntity<ApiResponse> getLessons(Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                List<LessonSummaryDto> lessons = lessonService.getTeacherLessons(teacher.getId());
                Map<String, Object> data = new HashMap<>();
                data.put("number", lessons.size());
                data.put("lessons", lessons);
                return ResponseEntity.ok(new ApiResponse(true, "lessons returned successfully!", data));
        }

        @GetMapping("/lesson/{lessonId}")
        public ResponseEntity<ApiResponse> getLesson(@PathVariable Long lessonId,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                LessonDetailsDto lesson = lessonService.getLesson(lessonId, teacher.getId());
                return ResponseEntity.ok(new ApiResponse(true, "lesson returned successfully!", lesson));
        }

        @GetMapping("/join-requests")
        public ResponseEntity<ApiResponse> getAllJoinRequests(Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                var list = teacherService.getAllPendingJoinRequests(teacher.getId());
                return ResponseEntity.ok(new ApiResponse(true,
                                "All pending join requests fetched successfully!", list));
        }

        @GetMapping("/exam/{examId}")
        public ResponseEntity<ApiResponse> getExamWithExamID(@PathVariable Long examId,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                var exam = examService.getExamByIdAsTeacher(examId, email);
                return ResponseEntity.ok(new ApiResponse(true,
                                "Exam details fetched successfully!", exam));
        }

        @GetMapping("/exam/answers/{studentExamId}")
        public ResponseEntity<ApiResponse> getStudentExamAnswers(@PathVariable Long studentExamId,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                var answers = examService.getStudentExamAnswers(teacher.getId(), studentExamId);
                return ResponseEntity.ok(new ApiResponse(true,
                                "Student exam answers fetched successfully!", answers));
        }

        @PostMapping("/exam/verify/{studentExamId}")
        public ResponseEntity<ApiResponse> verifyStudentExam(@PathVariable Long studentExamId,
                        @Valid @RequestBody List<AnswerMarkDto> answers,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

                examService.verifyStudentExam(teacher.getId(), studentExamId, answers);
                return ResponseEntity.ok(new ApiResponse(true,
                                "Student exam verified successfully!", null));
        }

        @PostMapping("/exam/{examId}/complete")
        public ResponseEntity<ApiResponse> markExamAsCompleted(@PathVariable Long examId,
                        Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new UnauthorizedException("Missing or invalid token!");
                }

                String email = authentication.getName();
                TeacherModel teacher = teacherRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("Teacher not found!"));



                examService.markExamAsCompleted(teacher.getId(), examId);
                return ResponseEntity.ok(new ApiResponse(true,
                                "Exam marked as completed and students notified!", null));
        }
}
