package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class QuestionBankModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    // شلنا الـ String lectureName وحطينا الربط الحقيقي بجدول الدروس
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private LessonModel lesson;

    private String correctAnswer;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private TeacherModel teacher;
}