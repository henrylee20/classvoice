package me.henrylee.classvoice.model;

import org.springframework.data.annotation.Id;

public class VoiceInfo {

    @Id
    private String id;
    private String path;
    private String content;
    private double accuracy;
    private String studentId;
    private String questionId;
    private String feedback;

    private String openPageTime;
    private String startRecTime;
    private String stopRecTime;
    private String recvTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getOpenPageTime() {
        return openPageTime;
    }

    public void setOpenPageTime(String openPageTime) {
        this.openPageTime = openPageTime;
    }

    public String getStartRecTime() {
        return startRecTime;
    }

    public void setStartRecTime(String startRecTime) {
        this.startRecTime = startRecTime;
    }

    public String getStopRecTime() {
        return stopRecTime;
    }

    public void setStopRecTime(String stopRecTime) {
        this.stopRecTime = stopRecTime;
    }

    public String getRecvTime() {
        return recvTime;
    }

    public void setRecvTime(String recvTime) {
        this.recvTime = recvTime;
    }
}
