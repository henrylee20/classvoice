package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.Question;

public interface QuestionService {
    Question getQuestionById(String questionId);

    Question addQuestion(Question question);

    Question modQuestion(Question question);
    boolean delQuestion(Question question);

    double compareWithAnswer(Question question, String answer);
}
