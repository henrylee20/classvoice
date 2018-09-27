package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private TokenRepository tokenRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserServiceImpl(TokenRepository tokenRepository,
                           StudentRepository studentRepository,
                           TeacherRepository teacherRepository) {
        this.tokenRepository = tokenRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public Token userLogin(String openid) {
        // remove exist tokens
        List<Token> existTokens = tokenRepository.findByOpenid(openid);
        if (!existTokens.isEmpty()) {
            for (Token existToken : existTokens) {
                tokenRepository.delete(existToken);
            }
        }

        // find student by openid
        User user;
        String userType;
        List<Student> students = studentRepository.findByOpenid(openid);
        if (students.isEmpty()) {
            logger.info("cannot find student by openid. openid: " + openid);

            // try find teacher by openid
            List<Teacher> teachers = teacherRepository.findByOpenid(openid);
            if (teachers.isEmpty()) {
                logger.info("cannot find teacher by openid. openid: " + openid);
                return null;
            } else {
                user = teachers.get(0);
                userType = "teacher";
            }
        } else {
            user = students.get(0);
            userType = "student";
        }

        // login, generate token
        Token token = new Token();
        token.setOpenid(openid);
        token.setUserId(user.getId());
        token.setUserType(userType);

        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject(openid).signWith(key).compact();
        token.setToken(jws);

        // save token
        tokenRepository.insert(token);

        return token;
    }

    @Override
    public boolean studentAdd(Student student) {

        if (!student.checkBaseInfo()) {
            logger.warn("base info is not enough");
            return false;
        }

        if (student.getOpenid() == null) {
            logger.warn("there is no openid");
            return false;
        }

        List<Student> students = studentRepository.findByOpenid(student.getOpenid());
        if (students.isEmpty()) {
            studentRepository.insert(student);
            return true;
        } else {
            logger.warn("student exist");
            return false;
        }
    }

    @Override
    public boolean teacherAdd(Teacher teacher) {
        if (!teacher.checkBaseInfo()) {
            logger.warn("base info is not enough");
            return false;
        }

        if (teacher.getOpenid() == null) {
            logger.warn("there is no openid");
            return false;
        }

        List<Teacher> teachers = teacherRepository.findByOpenid(teacher.getOpenid());
        if (teachers.isEmpty()) {
            teacherRepository.insert(teacher);
            return true;
        } else {
            logger.warn("teacher exist");
            return false;
        }
    }
}
