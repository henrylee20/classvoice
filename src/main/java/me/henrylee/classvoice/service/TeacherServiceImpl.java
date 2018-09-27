package me.henrylee.classvoice.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import me.henrylee.classvoice.model.*;
import me.henrylee.classvoice.model.Class;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service
public class TeacherServiceImpl implements TeacherService {

    private TokenRepository tokenRepository;
    private TeacherRepository teacherRepository;
    private StudentService studentService;
    private ClassService classService;
    private QuestionService questionService;

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TeacherServiceImpl(TokenRepository tokenRepository,
                              TeacherRepository teacherRepository,
                              StudentService studentService,
                              ClassService classService,
                              QuestionService questionService) {
        this.tokenRepository = tokenRepository;
        this.teacherRepository = teacherRepository;
        this.studentService = studentService;
        this.classService = classService;
        this.questionService = questionService;
    }

    @Override
    public Teacher getTeacher(String token) {
        List<Token> tokens = tokenRepository.findByToken(token);
        if (tokens.isEmpty()) {
            logger.warn("empty token");
            return null;
        }

        if (!tokens.get(0).getUserType().equals("teacher")) {
            logger.warn(String.format("assert user type [teacher]: %s, token: %s",
                    tokens.get(0).getUserType(), token));
            return null;
        }

        String teacherId = tokens.get(0).getUserId();

        Optional<Teacher> teacher = teacherRepository.findById(teacherId);
        if (!teacher.isPresent()) {
            logger.warn(String.format("not found teacher. token: %s", token));
            return null;
        }

//      if (Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject()
//              .equals(teacher.get().getOpenid())) {
//          logger.warn("JWT verify failed. token: %s", token);
//          return null;
//      }

        return teacher.orElse(null);
    }

    @Override
    public Class addClass(Teacher teacher, Class classInfo) {
        if (!classInfo.checkBaseInfo()) {
            return null;
        }

        // set class info
        classService.addClass(classInfo, teacher.getId());

        // update teacher info
        List<String> classIds = teacher.getClassIds();
        if (classIds == null) {
            classIds = new ArrayList<>();
        }

        classIds.add(classInfo.getId());
        teacher.setClassIds(classIds);
        teacherRepository.save(teacher);

        return classInfo;
    }

    @Override
    public List<Class> delClass(Teacher teacher, String classId) {
        if (teacher.getClassIds() == null || teacher.getClassIds().indexOf(classId) == -1) {
            logger.warn(String.format("del class failed. this class is not belong to teacher. classId: %s, teacherId: %s",
                    classId, teacher.getId()));
            return null;
        }

        Class clazz = classService.getClassById(classId);
        if (clazz == null) {
            logger.info("cannot get class. classId: {}", classId);
            return null;
        }

        // update teacher info
        List<String> classIds = teacher.getClassIds();
        classIds.remove(classId);
        teacher.setClassIds(classIds);
        teacherRepository.save(teacher);

        // students leave
        List<String> studentIds = clazz.getStudentIds();
        if (studentIds != null) {
            Student student;
            for (String studentId : studentIds) {
                student = studentService.getStudentById(studentId);
                studentService.leaveClass(student, classId, false);
            }
        }

        // del class
        classService.delClass(clazz, teacher.getId());


        return getClasses(teacher);
    }

    @Override
    public List<Class> getClasses(Teacher teacher) {
        List<String> classIds = teacher.getClassIds();

        if (classIds == null) {
            return new ArrayList<>();
        }

        List<Class> classes = new ArrayList<>();

        boolean isChanged = false;
        for (String classId : classIds) {
            Class clazz = classService.getClassById(classId);
            if (clazz != null) {
                classes.add(clazz);
            } else {
                classIds.remove(classId);
                isChanged = true;
            }
        }

        if (isChanged) {
            teacher.setClassIds(classIds);
            teacherRepository.save(teacher);
        }

        return classes;
    }

    @Override
    public List<Question> addQuestionToClass(Teacher teacher, String questionId, String classId) {
        if (teacher.getClassIds().indexOf(classId) == -1) {
            logger.info("Class is not belong to this teacher. classId: {}, teacherId: {}",
                    classId, teacher.getId());
            return null;
        }

        Class clazz = classService.getClassById(classId);
        if (clazz == null) {
            logger.info("Class not found. classId: {}", classId);
            return null;
        }

        if (classService.addQuestionToClass(questionId, clazz, teacher.getId())) {
            return classService.getQuestions(clazz);
        } else {
            logger.info("cannot add question into class. classId: {}, questionId: {}", classId, questionId);
            return null;
        }
    }

    @Override
    public List<Question> delQuestionFromClass(Teacher teacher, String questionId, String classId) {
        if (teacher.getClassIds().indexOf(classId) == -1) {
            logger.info("Class is not belong to this teacher. classId: {}, teacherId: {}",
                    classId, teacher.getId());
            return null;
        }

        Class clazz = classService.getClassById(classId);
        if (clazz == null) {
            logger.info("Class not found. classId: {}", classId);
            return null;
        }

        if (classService.delQuestionFromClass(questionId, clazz, teacher.getId())) {
            return classService.getQuestions(clazz);
        } else {
            logger.info("cannot del question from class. classId: {}, questionId: {}", classId, questionId);
            return null;
        }
    }

    @Override
    public List<Question> getQuestions(Teacher teacher, String classId) {
        if (teacher.getClassIds().indexOf(classId) == -1) {
            logger.info("class is not belong to this teacher. classId: {}, teacherId: {}",
                    classId, teacher.getId());
            return null;
        }

        Class clazz = classService.getClassById(classId);
        if (clazz == null) {
            logger.info("Class not found. classId: {}", classId);
            return null;
        }

        return classService.getQuestions(clazz);
    }

    @Override
    public boolean addStudentToClass(Teacher teacher, String studentId, String classId) {
        if (teacher.getClassIds() == null || teacher.getClassIds().indexOf(classId) == -1) {
            logger.warn(String.format("add student failed. this class is not belong to teacher. classId: %s, teacherId: %s",
                    classId, teacher.getId()));
            return false;
        }

        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            logger.info("student not found. studentId: {}", studentId);
            return false;
        }
        return studentService.joinClass(student, classId, true);
    }

    @Override
    public boolean delStudentFromClass(Teacher teacher, String studentId, String classId) {
        if (teacher.getClassIds() == null || teacher.getClassIds().indexOf(classId) == -1) {
            logger.warn(String.format("add student failed. this class is not belong to teacher. classId: %s, teacherId: %s",
                    classId, teacher.getId()));
            return false;
        }

        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            logger.info("student not found. studentId: {}", studentId);
            return false;
        }
        return studentService.leaveClass(student, classId, true);
    }

    @Override
    public List<Student> getStudents(Teacher teacher, String classId) {
        if (teacher.getClassIds() == null || teacher.getClassIds().indexOf(classId) == -1) {
            logger.warn(String.format("add student failed. this class is not belong to teacher. classId: %s, teacherId: %s",
                    classId, teacher.getId()));
            return null;
        }

        Class clazz = classService.getClassById(classId);
        if (clazz == null) {
            logger.info("class not found. studentId: {}", classId);
            return null;
        }

        return classService.getStudents(clazz);
    }
}
