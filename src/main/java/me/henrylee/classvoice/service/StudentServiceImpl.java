package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import me.henrylee.classvoice.model.Class;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

    private TokenRepository tokenRepository;
    private StudentRepository studentRepository;
    private ClassService classService;
    private QuestionService questionService;

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StudentServiceImpl(TokenRepository tokenRepository,
                              StudentRepository studentRepository,
                              ClassService classService,
                              QuestionService questionService) {
        this.tokenRepository = tokenRepository;
        this.studentRepository = studentRepository;
        this.classService = classService;
        this.questionService = questionService;
    }

    @Override
    public Student getStudentById(String studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (!student.isPresent()) {
            logger.info("student not found. studentId: {}", studentId);
        }
        return student.orElse(null);
    }

    @Override
    public Student getStudent(String token) {
        List<Token> tokens = tokenRepository.findByToken(token);
        if (tokens.isEmpty()) {
            logger.info("empty token");
            return null;
        }

        if (!tokens.get(0).getUserType().equals("student")) {
            logger.info("assert user type [student]: {}, token: {}", tokens.get(0).getUserType(), token);
            return null;
        }
        String studentId = tokens.get(0).getUserId();

        Optional<Student> student = studentRepository.findById(studentId);

        if (!student.isPresent()) {
            logger.info("student not found. token: {}", token);
            return null;
        }

//      if (Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject()
//              .equals(student.get().getOpenid())) {
//          logger.warn("JWT verify failed. token: %s", token);
//          return null;
//      }

        return student.orElse(null);
    }

    @Override
    public boolean joinClass(Student student, String classId, boolean registerToClass) {
        Class clazz = classService.getClassById(classId);
        if (clazz == null) {
            logger.info("class not found. classId: {}", classId);
            return false;
        }

        // update student info
        List<String> classIds = student.getClassIds();
        if (classIds == null) {
            classIds = new ArrayList<>();
        }
        if (classIds.indexOf(classId) == -1) {
            classIds.add(classId);
            student.setClassIds(classIds);
            studentRepository.save(student);
        }

        // register into class
        if (registerToClass) {
            classService.addStudent(clazz, student.getId());
        }

        return true;
    }

    @Override
    public boolean leaveClass(Student student, String classId, boolean registerToClass) {
        Class clazz = classService.getClassById(classId);
        if (clazz == null) {
            logger.info("class not found. classId: {}", classId);
            return false;
        }

        // update student info
        List<String> classIds = student.getClassIds();
        if (classIds == null || classIds.indexOf(classId) == -1) {
            return true;
        }
        classIds.remove(classId);
        student.setClassIds(classIds);
        studentRepository.save(student);

        // update class info
        if (registerToClass) {
            classService.delStudent(clazz, student.getId());
        }

        return true;
    }

    @Override
    public List<Class> getClasses(Student student) {
        List<String> classIds = student.getClassIds();
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
            student.setClassIds(classIds);
            studentRepository.save(student);
        }

        return classes;
    }

    @Override
    public List<Question> getQuestions(Student student, String classId) {
        if (student.getClassIds() == null || student.getClassIds().indexOf(classId) == -1) {
            logger.info("student is not in this class while get questions. studentId: {}, classId: {}",
                    student.getId(), classId);
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
    public boolean addVoice(Student student, String voiceId) {
        List<String> voiceIds = student.getVoiceIds();

        if (voiceIds == null) {
            voiceIds = new ArrayList<>();
        }

        voiceIds.add(voiceId);
        student.setVoiceIds(voiceIds);
        studentRepository.save(student);
        return true;
    }

    @Override
    public double getAccuracy(String questionId, String answer) {
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            return .0;
        }

        return questionService.compareWithAnswer(question, answer);
    }
}
