package me.henrylee.classvoice.model.response;

import me.henrylee.classvoice.model.Question;

import java.util.List;

public class QuestionListResponse extends Response {
    private List<Question> questions;

    public QuestionListResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public QuestionListResponse(ErrMsg errMsg, List<Question> questions) {
        super(errMsg);
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
