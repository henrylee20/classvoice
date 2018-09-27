package me.henrylee.classvoice.model.response;

import me.henrylee.classvoice.model.Student;

import java.util.List;

public class StudentListResponse extends Response {
    private List<Student> students;

    public StudentListResponse(ErrMsg errMsg) {
        super(errMsg);
    }

    public StudentListResponse(ErrMsg errMsg, List<Student> students) {
        super(errMsg);
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
