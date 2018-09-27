package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.Class;
import me.henrylee.classvoice.model.Question;
import me.henrylee.classvoice.model.Student;

import java.util.List;

public interface StudentService {
    Student getStudentById(String studentId);

    Student getStudent(String token);

    boolean joinClass(Student student, String classId, boolean registerToClass);

    boolean leaveClass(Student student, String classId, boolean registerToClass);

    List<Class> getClasses(Student student);

    List<Question> getQuestions(Student student, String classId);

    boolean addVoice(Student student, String voiceId);

    double getAccuracy(String questionId, String answer);
}
