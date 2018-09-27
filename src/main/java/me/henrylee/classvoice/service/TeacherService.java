package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.Class;
import me.henrylee.classvoice.model.Question;
import me.henrylee.classvoice.model.Student;
import me.henrylee.classvoice.model.Teacher;

import java.util.List;

public interface TeacherService {
    Teacher getTeacher(String token);

    Class addClass(Teacher teacher, Class classInfo);

    List<Class> delClass(Teacher teacher, String classId);

    List<Class> getClasses(Teacher teacher);

    List<Question> addQuestionToClass(Teacher teacher, String questionId, String classId);

    List<Question> delQuestionFromClass(Teacher teacher, String questionId, String classId);

    List<Question> getQuestions(Teacher teacher, String classId);

    boolean addStudentToClass(Teacher teacher, String studentId, String classId);

    boolean delStudentFromClass(Teacher teacher, String studentId, String classId);

    List<Student> getStudents(Teacher teacher, String classId);
}
