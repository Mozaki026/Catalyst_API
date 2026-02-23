package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.ExamModel;
import com.meshwarcoders.catalyst.api.model.StudentExamModel;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExamModel, Long> {
    Optional<StudentExamModel> findByStudentAndExam(StudentModel student, ExamModel exam);

    List<StudentExamModel> findByExam(ExamModel exam);

    List<StudentExamModel> findByStudentAndExamIn(StudentModel student, List<ExamModel> exams);
}
