package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meshwarcoders.catalyst.api.model.common.ExamType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "exams")
@Getter
@Setter
public class ExamModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String examName;

    @ManyToOne(optional = false)
    private LessonModel lesson;

    private Integer maxGrade;

    private ExamType examType;

    private LocalDateTime examDateTime;
    private LocalDateTime closingDate;

    private Integer durationMinutes;

    @JsonIgnore
    @OneToMany(mappedBy = "exam")
    private List<StudentExamModel> studentExams = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "exam")
    private List<ExamQuestionModel> questions = new ArrayList<>();

    private Boolean completed = false;
}
