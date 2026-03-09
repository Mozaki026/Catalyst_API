package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "student_answers")
@Getter
@Setter
public class StudentAnswerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false)
    private StudentExamModel studentExam;

    @ManyToOne(optional = false)
    private QuestionModel question;

    @ElementCollection
    private List<Integer> selectedOptions;

    @Column(columnDefinition = "TEXT")
    private String textAnswer;

    private Double mark;
}
