package me.henrylee.classvoice.controller;

import me.henrylee.classvoice.model.Class;
import me.henrylee.classvoice.model.Question;
import me.henrylee.classvoice.model.Student;
import me.henrylee.classvoice.model.VoiceInfo;
import me.henrylee.classvoice.model.response.*;
import me.henrylee.classvoice.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@CrossOrigin
@RestController
@RequestMapping("/student")
public class StudentController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private StudentService studentService;
    private ClassService classService;
    private QuestionService questionService;
    private VoiceInfoService voiceInfoService;

    private NLPService nlpService;

    @Autowired
    public StudentController(StudentService service,
                             ClassService classService,
                             QuestionService questionService,
                             VoiceInfoService voiceInfoService,
                             NLPService nlpService) {
        this.studentService = service;
        this.classService = classService;
        this.questionService = questionService;
        this.voiceInfoService = voiceInfoService;
        this.nlpService = nlpService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/getAllClasses")
    public ClassListResponse getAllClasses(@PathVariable String token) {
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            return new ClassListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<Class> classes = classService.getAllClass();
        if (classes == null) {
            logger.warn("Cannot get all class. studentId: {}", student.getId());
            return new ClassListResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new ClassListResponse(ErrMsg.OK, classes);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/getClasses")
    public ClassListResponse getClasses(@PathVariable String token) {
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            return new ClassListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<Class> classes = studentService.getClasses(student);
        if (classes == null) {
            logger.warn("Cannot get class. studentId: {}", student.getId());
            return new ClassListResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new ClassListResponse(ErrMsg.OK, classes);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/joinClass")
    public ClassListResponse joinClass(@PathVariable String token, @RequestParam("classId") String classId) {
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            return new ClassListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        if (studentService.joinClass(student, classId, true)) {
            List<Class> classes = studentService.getClasses(student);
            return new ClassListResponse(ErrMsg.OK, classes);
        } else {
            logger.warn("Cannot join class. studentId: {}, classId: {}", student.getId(), classId);
            return new ClassListResponse(ErrMsg.DB_OP_ERR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/leaveClass")
    public ClassListResponse leaveClass(@PathVariable String token,
                                        @RequestParam("classId") String classId) {
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            return new ClassListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        if (studentService.leaveClass(student, classId, true)) {
            List<Class> classes = studentService.getClasses(student);
            return new ClassListResponse(ErrMsg.OK, classes);
        } else {
            logger.warn("Cannot leave class. studentId: {}, classId: {}", student.getId(), classId);
            return new ClassListResponse(ErrMsg.DB_OP_ERR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/getQuestions")
    public QuestionListResponse getQuestions(@PathVariable String token,
                                             @RequestParam("classId") String classId) {
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            return new QuestionListResponse(ErrMsg.USER_TOKEN_ERR);
        }


        List<Question> questions = studentService.getQuestions(student, classId);
        if (questions == null) {
            logger.info("get question failed. studentId: {}, classId: {}", student.getId(), classId);
            return new QuestionListResponse(ErrMsg.DB_OP_ERR);
        } else {
            return new QuestionListResponse(ErrMsg.OK, questions);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{token}/uploadVoice")
    public StringResponse uploadVoice(@PathVariable String token,
                                      @RequestParam("questionId") String questionId,
                                      @RequestParam("openPageTime") String openPageTime,
                                      @RequestParam("startRecTime") String startRecTime,
                                      @RequestParam("stopRecTime") String stopRecTime,
                                      @RequestParam("uploadFile") MultipartFile file) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // auth token
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            return new StringResponse(ErrMsg.USER_TOKEN_ERR);
        }

        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            logger.info("question not found");
            return new StringResponse(ErrMsg.DB_DATA_NOT_FOUND);
        }

        if (file == null || file.isEmpty()) {
            logger.warn("empty file");
            return new StringResponse(ErrMsg.FILE_EMPTY);
        }


        byte[] voiceData;
        try {
            voiceData = file.getBytes();
        } catch (IOException e) {
            logger.warn("read file err");
            return new StringResponse(ErrMsg.SERVER_ERR);
        }

        VoiceInfo voiceInfo = new VoiceInfo();

        voiceInfo.setStudentId(student.getId());
        voiceInfo.setQuestionId(questionId);
        voiceInfo.setOpenPageTime(openPageTime);
        voiceInfo.setStartRecTime(startRecTime);
        voiceInfo.setStopRecTime(stopRecTime);

        voiceInfo = voiceInfoService.saveVoice(voiceData, voiceInfo);

        Future<String> asrResult = voiceInfoService.ASR(voiceInfo);

        if (voiceInfo == null) {
            logger.info("save voice failed. studentId: {}, questionId: {}", student.getId(), questionId);
            return new StringResponse(ErrMsg.SERVER_ERR);
        }

        studentService.addVoice(student, voiceInfo.getId());

        try {
            if (asrResult.isDone()) {
                voiceInfo.setContent(asrResult.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("asr mission is interrupted. msg: {}", e.getMessage());
        }

        return new StringResponse(ErrMsg.OK, voiceInfo.getContent());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/downloadVoice")
    public byte[] downloadVoice(@PathVariable String token,
                                @RequestParam("voiceId") String voiceId) {
        // auth token
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            //return new BinaryResponse(ErrMsg.USER_TOKEN_ERR);
            return null;
        }

        List<String> voiceIds = student.getVoiceIds();
        if (voiceIds.indexOf(voiceId) < 0) {
            logger.info("this voice is not belong to this student. studentId: {}, voiceId: {}",
                    student.getId(), voiceId);
            //return new BinaryResponse(ErrMsg.USER_PERMISSION_NOT_ALLOWED);
            return null;
        }

        byte[] data = voiceInfoService.getMP3Voice(voiceId);
        if (data == null) {
            logger.info("provide mp3 file error");
            //return new BinaryResponse(ErrMsg.SERVER_ERR);
            return null;
        }

        //return new BinaryResponse(ErrMsg.OK, data);
        return data;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/getVoiceList")
    public VoiceInfoListResponse getVoiceList(@PathVariable String token) {

        // auth token
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            return new VoiceInfoListResponse(ErrMsg.USER_TOKEN_ERR);
        }

        List<UserVoiceInfo> result = new ArrayList<>();
        List<String> voiceIds = student.getVoiceIds();
        if (voiceIds == null) {
            voiceIds = new ArrayList<>();
        }
        for (String voiceId : voiceIds) {
            VoiceInfo voiceInfo = voiceInfoService.getVoiceInfo(voiceId);
            if (voiceInfo == null) {
                continue;
            }

            UserVoiceInfo info = new UserVoiceInfo();
            info.setVoiceId(voiceInfo.getId());
            if (voiceInfo.getContent() != null && !voiceInfo.getContent().equals("")) {
                info.setAnswer(voiceInfo.getContent());
            } else {
                logger.info("empty voice content. start ASR. voiceId: {}", voiceId);
                try {
                    Future<String> asrResult = voiceInfoService.ASR(voiceInfo);
                    while (!asrResult.isDone())
                        Thread.sleep(1000);
                    info.setAnswer(asrResult.get());
                } catch (InterruptedException | ExecutionException e) {
                    logger.warn("ASR thread err. e.msg: {}", e.getMessage());
                }
                logger.info("ASR finished. voiceId: {}", voiceId);
            }

            Question question = questionService.getQuestionById(voiceInfo.getQuestionId());
            if (question == null) {
                continue;
            }

            info.setQuestionId(question.getId());
            info.setQuestion(question.getDesc());

            result.add(info);
        }

        return new VoiceInfoListResponse(ErrMsg.OK, result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{token}/feedbackVoice")
    public VoiceInfoResponse feedbackVoice(@PathVariable String token,
                                           @RequestParam("voiceId") String voiceId,
                                           @RequestParam("feedback") String feedback) {
        // auth token
        Student student = studentService.getStudent(token);
        if (student == null) {
            logger.info("token auth failed. token: " + token);
            return new VoiceInfoResponse(ErrMsg.USER_TOKEN_ERR);
        }

        if (student.getVoiceIds() == null || student.getVoiceIds().indexOf(voiceId) < 0) {
            logger.info("student don't have this voice. studentId: {}, voiceId: {}", student.getId(), voiceId);
            return new VoiceInfoResponse(ErrMsg.USER_PERMISSION_NOT_ALLOWED);
        }

        VoiceInfo voiceInfo = voiceInfoService.feedback(voiceId, feedback);
        if (voiceInfo == null) {
            logger.warn("feed back failed");
            return new VoiceInfoResponse(ErrMsg.DB_OP_ERR);
        }

        UserVoiceInfo info = new UserVoiceInfo();
        info.setVoiceId(voiceInfo.getId());
        info.setAnswer(voiceInfo.getContent());
        info.setFeedback(voiceInfo.getFeedback());

        Question question = questionService.getQuestionById(voiceInfo.getQuestionId());
        if (question == null) {
            logger.warn("voice question not found. voiceId: {}, questionId:{}",
                    voiceInfo.getId(), voiceInfo.getQuestionId());
            return new VoiceInfoResponse(ErrMsg.DB_DATA_NOT_FOUND);
        }

        info.setQuestionId(question.getId());
        info.setQuestion(question.getDesc());

        return new VoiceInfoResponse(ErrMsg.OK, info);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAccuracyTmp")
    public StringResponse getAccuracyTmp(@RequestParam("answer") String answer,
                                         @RequestParam("questionId") String questionId) {

        double result = 0.5; // TODO now is a fake interface. studentService.getAccuracy(questionId, answer);
        return new StringResponse(ErrMsg.OK, (new Double(result)).toString());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getWordsTmp")
    public StringResponse getWordsTmp(@RequestParam("answer") String answer) {

        Future<List<String>> words = nlpService.getWords(answer);

        try {
            List<String> result = words.get(5, TimeUnit.SECONDS);
            return new StringResponse(ErrMsg.OK, result.toString());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("cut Error. msg: {}", e.getMessage());
            return new StringResponse(ErrMsg.OK, "");
        } catch (TimeoutException e) {
            logger.error("cut timeout. msg: {}", e.getMessage());
            return new StringResponse(ErrMsg.OK, "timeout");
        }
    }
}
