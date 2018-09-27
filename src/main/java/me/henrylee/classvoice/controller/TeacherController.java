package me.henrylee.classvoice.controller;

import me.henrylee.classvoice.model.Class;
import me.henrylee.classvoice.model.Question;
import me.henrylee.classvoice.model.Student;
import me.henrylee.classvoice.model.Teacher;
import me.henrylee.classvoice.model.response.*;
import me.henrylee.classvoice.service.QuestionService;
import me.henrylee.classvoice.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private TeacherService teacherService;
    private QuestionService questionService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TeacherController(TeacherService teacherService,
                             QuestionService questionService) {
        this.teacherService = teacherService;
        this.questionService = questionService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{token}/addClass")
    public ClassResponse addClass(@PathVariable String token, @RequestBody Class classInfo) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new ClassResponse(ErrMsg.USER_TOKEN_ERR);
        }

        Class result = teacherService.addClass(teacher, classInfo);
        if (result == null) {
            logger.warn("Cannot add class. class: {}", classInfo.toString());
            return new ClassResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new ClassResponse(ErrMsg.OK, result);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/delClass")
    public ClassListResponse delClass(@PathVariable String token,
                                      @RequestParam("classId") String classId) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new ClassListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<Class> result = teacherService.delClass(teacher, classId);
        if (result == null) {
            logger.warn("Cannot del class. classId: {}", classId);
            return new ClassListResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new ClassListResponse(ErrMsg.OK, result);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/getClasses")
    public ClassListResponse getClasses(@PathVariable String token) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new ClassListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<Class> result = teacherService.getClasses(teacher);
        if (result == null) {
            logger.warn("Cannot get classes. teacherId: {}", teacher.getId());
            return new ClassListResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new ClassListResponse(ErrMsg.OK, result);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{token}/addQuestion")
    public QuestionResponse addQuestion(@PathVariable String token,
                                        @RequestBody Question question) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new QuestionResponse(ErrMsg.USER_TOKEN_ERR);
        }

        question = questionService.addQuestion(question);
        if (question == null) {
            logger.warn("add question to question db failed.");
            return new QuestionResponse(ErrMsg.DB_OP_ERR);
        }

        return new QuestionResponse(ErrMsg.OK, question);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/addQuestionToClass")
    public QuestionListResponse addQuestionToClass(@PathVariable String token,
                                                   @RequestParam("classId") String classId,
                                                   @RequestParam("questionId") String questionId) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new QuestionListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<Question> questions = teacherService.addQuestionToClass(teacher, questionId, classId);
        if (questions == null) {
            logger.warn("add question to question class failed. questionId: {}, classId: {}", questionId, classId);
            return new QuestionListResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new QuestionListResponse(ErrMsg.OK, questions);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/delQuestionFromClass")
    public QuestionListResponse delQuestionFromClass(@PathVariable String token,
                                                     @RequestParam("classId") String classId,
                                                     @RequestParam("questionId") String questionId) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new QuestionListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<Question> questions = teacherService.delQuestionFromClass(teacher, questionId, classId);
        if (questions == null) {
            logger.warn("del question from question class failed. questionId: {}, classId: {}", questionId, classId);
            return new QuestionListResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new QuestionListResponse(ErrMsg.OK, questions);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/getQuestions")
    public QuestionListResponse getQuestions(@PathVariable String token,
                                             @RequestParam("classId") String classId) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new QuestionListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<Question> questions = teacherService.getQuestions(teacher, classId);
        if (questions == null) {
            logger.warn("get question failed. classId: {}", classId);
            return new QuestionListResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new QuestionListResponse(ErrMsg.OK, questions);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/addStudentToClass")
    public StudentListResponse addStudentToClass(@PathVariable String token,
                                                 @RequestParam("studentId") String studentId,
                                                 @RequestParam("classId") String classId) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new StudentListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        if (teacherService.addStudentToClass(teacher, studentId, classId)) {
            return new StudentListResponse(ErrMsg.OK, teacherService.getStudents(teacher, classId));
        } else {
            return new StudentListResponse(ErrMsg.DB_DATA_NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/delStudentFromClass")
    public StudentListResponse delStudentFromClass(@PathVariable String token,
                                                   @RequestParam("studentId") String studentId,
                                                   @RequestParam("classId") String classId) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new StudentListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        if (teacherService.delStudentFromClass(teacher, studentId, classId)) {
            return new StudentListResponse(ErrMsg.OK, teacherService.getStudents(teacher, classId));
        } else {
            return new StudentListResponse(ErrMsg.DB_DATA_NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/getStudents")
    public StudentListResponse getStudents(@PathVariable String token,
                                           @RequestParam("classId") String classId) {
        Teacher teacher = teacherService.getTeacher(token);
        if (teacher == null) {
            logger.warn("token auth failed. token: " + token);
            return new StudentListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<Student> students = teacherService.getStudents(teacher, classId);
        if (students == null) {
            return new StudentListResponse(ErrMsg.DB_DATA_NOT_FOUND);
        } else {
            return new StudentListResponse(ErrMsg.OK, students);
        }
    }
}
