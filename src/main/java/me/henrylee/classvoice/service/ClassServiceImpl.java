package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.*;
import me.henrylee.classvoice.model.Class;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ClassServiceImpl implements ClassService {

    private ClassRepository classRepository;
    private StudentRepository studentRepository;
    private QuestionService questionService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ClassServiceImpl(ClassRepository classRepository,
                            StudentRepository studentRepository,
                            QuestionService questionService) {
        this.classRepository = classRepository;
        this.questionService = questionService;
        this.studentRepository = studentRepository;
    }

    @Override
    public Class getClassById(String classId) {
        Optional<Class> clazz = classRepository.findById(classId);
        if (!clazz.isPresent()) {
            logger.info("class not found. classId: {}", classId);
        }

        return clazz.orElse(null);
    }

    @Override
    public List<Class> getAllClass() {
        return classRepository.findAll();
    }

    @Override
    public boolean addQuestionToClass(String questionId, Class clazz, String teacherId) {
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            logger.info("question not found. qustionId: {}", questionId);
            return false;
        }
        if (!clazz.getTeacherId().equals(teacherId)) {
            logger.info("class is not belong to this teacher. classId: {}, teacherId: {}", clazz.getId(), teacherId);
            return false;
        }

        List<String> questionIds = clazz.getQuestionIds();
        if (questionIds == null) {
            questionIds = new ArrayList<>();
        }
        if (questionIds.indexOf(questionId) == -1) {
            questionIds.add(questionId);
            clazz.setQuestionIds(questionIds);
            classRepository.save(clazz);
        }

        return true;
    }

    @Override
    public boolean delQuestionFromClass(String questionId, Class clazz, String teacherId) {
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            logger.info("question not found. qustionId: {}", questionId);
            return false;
        }
        if (!clazz.getTeacherId().equals(teacherId)) {
            logger.info("class is not belong to this teacher. classId: {}, teacherId: {}", clazz.getId(), teacherId);
            return false;
        }

        List<String> questionIds = clazz.getQuestionIds();
        if (questionIds == null) {
            return true;
        } else if (questionIds.indexOf(questionId) != -1) {
            questionIds.remove(questionId);
            clazz.setQuestionIds(questionIds);
            classRepository.save(clazz);
        }

        return true;
    }

    @Override
    public List<Question> getQuestions(Class clazz) {
        // delete expired questionIds while getting question list
        boolean isChanged = false;
        List<Question> questions = new ArrayList<>();
        List<String> questionIds = clazz.getQuestionIds();

        if (questionIds == null) {
            logger.warn("cannot get questions. classId: {}", clazz.getId());

        }

        Iterator<String> iter = questionIds.iterator();
        while (iter.hasNext()) {
            String questionId = iter.next();
            Question question = questionService.getQuestionById(questionId);
            if (question != null &&
                    question.getType() != null) {
                questions.add(question);
            } else {
                logger.info("detected an question which not exist or doesn't have type. questionId: {}", questionId);
                iter.remove();
                isChanged = true;
            }
        }

        if (isChanged) {
            clazz.setQuestionIds(questionIds);
            classRepository.save(clazz);
        }

        return questions;
    }

    @Override
    public Class addClass(Class classInfo, String teacherId) {
        if (!classInfo.checkBaseInfo()) {
            logger.info("Class base info not set. Class info: {}", classInfo.toString());
            return null;
        }

        classInfo.setTeacherId(teacherId);

        Class classInfoResult = classRepository.insert(classInfo);
        if (classInfoResult == null) {
            logger.warn("insert class into db failed. Class info: {}", classInfo.toString());
        }

        return classInfoResult;
    }

    @Override
    public boolean delClass(Class clazz, String teacherId) {
        if (!clazz.getTeacherId().equals(teacherId)) {
            logger.info("class is not belong to this teacher. classId: {}, teacherId: {}", clazz.getId(), teacherId);
            return false;
        }

        classRepository.deleteById(clazz.getId());
        return true;
    }

    @Override
    public boolean addStudent(Class clazz, String studentId) {
        List<String> studentIds = clazz.getStudentIds();
        if (studentIds == null) {
            studentIds = new ArrayList<>();
        }
        if (studentIds.indexOf(studentId) == -1) {
            studentIds.add(studentId);
            clazz.setStudentIds(studentIds);
            classRepository.save(clazz);
        }

        return true;
    }

    @Override
    public boolean delStudent(Class clazz, String studentId) {
        List<String> studentIds = clazz.getStudentIds();
        if (studentIds == null) {
            return true;
        } else if (studentIds.indexOf(studentId) != -1) {
            studentIds.remove(studentId);
            clazz.setStudentIds(studentIds);
            classRepository.save(clazz);
        }

        return true;
    }

    @Override
    public List<Student> getStudents(Class clazz) {
        List<Student> students = new ArrayList<>();
        List<String> studentIds = clazz.getStudentIds();

        if (studentIds == null) {
            return students;
        }

        // delete expired studentIds while getting student list
        boolean isChanged = false;
        for (String studentId : studentIds) {
            Optional<Student> student = studentRepository.findById(studentId);
            if (!student.isPresent()) {
                studentIds.remove(studentId);
                isChanged = true;
            } else {
                students.add(student.get());
            }
        }

        if (isChanged) {
            clazz.setStudentIds(studentIds);
            classRepository.save(clazz);
        }

        return students;
    }
}
