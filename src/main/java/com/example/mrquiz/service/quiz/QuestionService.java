package com.example.mrquiz.service.quiz;

import com.example.mrquiz.entity.quiz.Question;
import com.example.mrquiz.repository.quiz.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Question findQuestionEntityById(UUID questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }
}