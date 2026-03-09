package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, String> {
    List<ExamSession> findAllByOrderByCreatedAtDesc();
}