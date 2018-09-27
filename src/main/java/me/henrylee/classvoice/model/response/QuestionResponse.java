package me.henrylee.classvoice.model.response;

import me.henrylee.classvoice.model.Question;

public class QuestionResponse extends Response {
    private Question question;

    public QuestionResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public QuestionResponse(ErrMsg errMsg, Question question) {
        super(errMsg);
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
