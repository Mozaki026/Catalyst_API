package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.request.CreateLessonRequest;
import com.meshwarcoders.catalyst.api.dto.request.LessonScheduleRequest;
import com.meshwarcoders.catalyst.api.dto.response.*;
import com.meshwarcoders.catalyst.api.exception.BadRequestException;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import com.meshwarcoders.catalyst.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LessonService {
        @Autowired
        private LessonRepository lessonRepository;

        @Autowired
        private LessonScheduleRepository lessonScheduleRepository;

        @Autowired
        private TeacherRepository teacherRepository;

        @Autowired
        private StudentRepository studentRepository;

        @Autowired
        private StudentLessonRepository studentLessonRepository;

        @Transactional
        public LessonSummaryDto createLesson(Long teacherId, CreateLessonRequest request) {
                LessonModel lesson = new LessonModel();

                TeacherModel teacher = teacherRepository.findById(teacherId)
                                .orElseThrow(() -> new NotFoundException("Teacher Not Found!"));
                lesson.setTeacher(teacher);
                lesson.setSubject(request.subject());
                lessonRepository.save(lesson);
                List<LessonScheduleModel> lessonSchedules = new ArrayList<>();
                if (request.lessonSchedules() != null) {
                        for (LessonScheduleRequest ls : request.lessonSchedules()) {
                                LessonScheduleModel lsm = new LessonScheduleModel();
                                lsm.setLesson(lesson);
                                lsm.setDuration(ls.duration());
                                if (ls.startTime() != null && !ls.startTime().isBlank()) {

                                        try {
                                                lsm.setStartTime(LocalTime.parse(ls.startTime()));
                                        } catch (DateTimeParseException e) {
                                                throw new BadRequestException("Invalid start time: " + ls.startTime());
                                        }

                                }
                                lsm.setDay(ls.day());
                                lessonSchedules.add(lsm);
                        }
                        lessonScheduleRepository.saveAll(lessonSchedules);
                }
                List<LessonScheduleDto> lessonScheduleDto = lessonSchedules.stream()
                                .map(ls -> new LessonScheduleDto(ls.getStartTime().toString(), ls.getDay(),
                                                ls.getDuration()))
                                .toList();
                TeacherDetailsDto teacherDto = new TeacherDetailsDto(teacher.getId(), teacher.getFullName());
                return new LessonSummaryDto(lesson.getId(), lesson.getSubject(), teacherDto, lessonScheduleDto);
        }

        @Transactional
        public LessonDetailsDto getLesson(Long lessonId, Long teacherId) {
                LessonModel lesson = lessonRepository.findById(lessonId)
                                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

                if (!lesson.getTeacher().getId().equals(teacherId)) {
                        throw new UnauthorizedException("You do not own this lesson!");
                }

                return getLessonDetails(lesson);
        }

        public List<LessonSummaryDto> getTeacherLessons(Long teacherId) {
                return lessonRepository.findByTeacherId(teacherId)
                                .stream()
                                .map(lesson -> {
                                        List<LessonScheduleModel> lessonSchedules = lessonScheduleRepository
                                                        .findByLessonId(lesson.getId());

                                        List<LessonScheduleDto> lessonScheduleDto = lessonSchedules.stream()
                                                        .map(ls -> new LessonScheduleDto(ls.getStartTime().toString(),
                                                                        ls.getDay(),
                                                                        ls.getDuration()))
                                                        .toList();
                                        TeacherModel teacher = lesson.getTeacher();
                                        TeacherDetailsDto teacherDto = new TeacherDetailsDto(teacher.getId(),
                                                        teacher.getFullName());
                                        return new LessonSummaryDto(lesson.getId(), lesson.getSubject(), teacherDto,
                                                        lessonScheduleDto);
                                })
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<LessonSummaryDto> getAllLessons() {
                return lessonRepository.findAll()
                                .stream()
                                .map(this::toSummaryDto)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<LessonSummaryDto> getStudentLessons(String studentEmail) {
                StudentModel student = studentRepository.findByEmail(studentEmail)
                                .orElseThrow(() -> new NotFoundException("Student not found!"));

                return studentLessonRepository.findByStudentAndStatus(student, EnrollmentStatus.APPROVED)
                                .stream()
                                .map(sl -> toSummaryDto(sl.getLesson()))
                                .toList();
        }

        @Transactional(readOnly = true)
        public LessonDetailsDto getStudentLessonDetails(Long lessonId, String studentEmail) {
                StudentModel student = studentRepository.findByEmail(studentEmail)
                                .orElseThrow(() -> new NotFoundException("Student not found!"));

                LessonModel lesson = lessonRepository.findById(lessonId)
                                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

                // Check if student is approved in this lesson
                studentLessonRepository.findByLessonAndStudent(lesson, student)
                                .filter(sl -> sl.getStatus() == EnrollmentStatus.APPROVED)
                                .orElseThrow(() -> new UnauthorizedException("You are not approved in this lesson!"));

                return getLessonDetails(lesson);
        }

        private LessonSummaryDto toSummaryDto(LessonModel lesson) {
                List<LessonScheduleDto> schedule = lesson.getLessonSchedules().stream()
                                .map(ls -> new LessonScheduleDto(ls.getStartTime().toString(), ls.getDay(),
                                                ls.getDuration()))
                                .toList();
                TeacherModel teacher = lesson.getTeacher();
                TeacherDetailsDto teacherDto = new TeacherDetailsDto(teacher.getId(), teacher.getFullName());
                return new LessonSummaryDto(lesson.getId(), lesson.getSubject(), teacherDto, schedule);
        }

        private LessonDetailsDto getLessonDetails(LessonModel lesson) {
                List<LessonScheduleDto> lessonScheduleDto = lesson.getLessonSchedules().stream()
                                .map(ls -> new LessonScheduleDto(ls.getStartTime().toString(), ls.getDay(),
                                                ls.getDuration()))
                                .toList();

                TeacherModel teacher = lesson.getTeacher();
                TeacherDetailsDto teacherDetailsDto = new TeacherDetailsDto(teacher.getId(), teacher.getFullName());

                List<StudentDetailsDto> studentsDetails = lesson.getStudentLessons()
                                .stream().map(s -> {
                                        StudentModel student = s.getStudent();
                                        return new StudentDetailsDto(
                                                        student.getId(), student.getFullName());
                                })
                                .toList();

                List<ExamSummaryDto> examSummaryDtos = lesson.getExams().stream().map(exam -> new ExamSummaryDto(
                                exam.getId(), lesson.getId(),
                                exam.getExamName(), exam.getMaxGrade(),
                                exam.getExamDateTime() != null ? exam.getExamDateTime().toString() : null,
                                exam.getClosingDate() != null ? exam.getClosingDate().toString() : null,
                                exam.getDurationMinutes(), exam.getExamType(), exam.getCompleted())).toList();

                LessonStudentsDto lessonStudentsDto = new LessonStudentsDto(studentsDetails.size(), studentsDetails);
                LessonExamsDto lessonExamsDto = new LessonExamsDto(examSummaryDtos.size(), examSummaryDtos);
                return new LessonDetailsDto(lesson.getId(), lesson.getSubject(), teacherDetailsDto, lessonStudentsDto,
                                lessonScheduleDto, lessonExamsDto);
        }
}
