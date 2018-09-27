package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.Student;
import me.henrylee.classvoice.model.Teacher;
import me.henrylee.classvoice.model.Token;

public interface UserService {
    Token userLogin(String openid);

    boolean studentAdd(Student student);

    boolean teacherAdd(Teacher teacher);
}
