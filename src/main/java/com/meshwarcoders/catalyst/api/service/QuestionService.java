package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.request.QuestionRequest;
import com.meshwarcoders.catalyst.api.dto.response.QuestionDto;
import com.meshwarcoders.catalyst.api.dto.response.QuestionResponseDto;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.QuestionModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    // ================== MAPPER ==================
    private QuestionDto mapToDto(QuestionModel q) {

        return  new QuestionDto(q.getId(), q.getText(),q.getType(), q.getOptions(),q.getMaxPoints());

    }

    // ================== ADD QUESTION ==================
    public void  addQuestion(Long teacherId,List<QuestionRequest> questions , Long lessonId) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        List <QuestionModel> questionModels = new ArrayList<QuestionModel>();

            for (QuestionRequest q : questions) {
                QuestionModel qm = new QuestionModel();
                qm.setLesson(lesson);
                qm.setText(q.text());
                qm.setType(q.type());
                qm.setOptions(q.options());
                qm.setCorrectOptionIndex(q.correctOptionIndex());
                qm.setMaxPoints(q.maxPoints());
                qm.setAnswer(q.answer());
                questionModels.add(qm) ;
            }
            questionRepository.saveAll(questionModels);
    }

    // ================== GET QUESTIONS ==================
    public List<QuestionDto> getQuestionsByLesson(Long teacherId, Long lessonId) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }
        return questionRepository.findByLessonId(lessonId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }
}