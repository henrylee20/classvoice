package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.Class;
import me.henrylee.classvoice.model.Question;
import me.henrylee.classvoice.model.Student;

import java.util.List;

public interface ClassService {
    Class getClassById(String classId);

    List<Class> getAllClass();

    boolean addQuestionToClass(String questionId, Class clazz, String teacherId);

    boolean delQuestionFromClass(String questionId, Class clazz, String teacherId);

    List<Question> getQuestions(Class clazz);


    Class addClass(Class classInfo, String teacherId);

    boolean delClass(Class clazz, String teacherId);

    boolean addStudent(Class clazz, String studentId);

    boolean delStudent(Class clazz, String studentId);

    List<Student> getStudents(Class clazz);
}
