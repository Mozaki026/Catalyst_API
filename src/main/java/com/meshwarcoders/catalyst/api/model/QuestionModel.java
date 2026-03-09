package com.meshwarcoders.catalyst.api.model;

import com.meshwarcoders.catalyst.api.model.common.QuestionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "exam_questions")
@Getter @Setter
public class QuestionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    private LessonModel lesson;

    @Column(nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    // For MCQ questions
    @ElementCollection
    private List<String> options = new ArrayList<>();
    @ElementCollection
    private List<Integer> correctOptionIndex = new ArrayList<>();

    private Integer maxPoints;

    private String answer;
}