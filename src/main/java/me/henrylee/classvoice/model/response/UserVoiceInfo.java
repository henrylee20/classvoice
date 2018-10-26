package me.henrylee.classvoice.model.response;

public class UserVoiceInfo {
    private String questionId;
    private String question;
    private String voiceId;
    private String answer;
    private double asrAccuracy;
    private String feedback;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getAnswer() {
        return answer;
    }

    public double getAsrAccuracy() {
        return asrAccuracy;
    }

    public void setAsrAccuracy(double asrAccuracy) {
        this.asrAccuracy = asrAccuracy;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
