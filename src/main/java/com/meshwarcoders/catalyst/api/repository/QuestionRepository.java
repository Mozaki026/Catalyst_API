package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.QuestionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionModel, Long> {
   List<QuestionModel> findByLessonId (Long lessonId );
}