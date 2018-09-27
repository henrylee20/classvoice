package me.henrylee.classvoice.model;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Class {

    @Id
    private String id;
    private String name;
    private String type;
    private String major;
    private String teacherId;
    private List<String> studentIds;
    private List<String> questionIds;
    private String memo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public boolean checkBaseInfo() {
        if (name == "") {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("id: %s, name: %s, type: %s, major: %s, teacherId: %s, studentIds: %s, questionIds: %s, memo: %s",
                id, name, type, major, teacherId, studentIds.toString(), questionIds.toString(), memo);
    }
}
