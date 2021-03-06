package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.Question;
import me.henrylee.classvoice.model.QuestionRepository;
import me.henrylee.classvoice.nlp.Segmenter;
import me.henrylee.classvoice.nlp.SegmenterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
public class QuestionServiceImpl implements QuestionService {

    private String nlpClassName = "me.henrylee.classvoice.nlp.WordSeg";

    private Future<Boolean> isInitFinished;

    private QuestionRepository questionRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Segmenter segmenter = null;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
//      segmenter = SegmenterFactory.getWordSeg(nlpClassName);
//      if (segmenter == null) {
//          logger.error("Cannot get Segmenter class. className: {}", nlpClassName);
//      }
//      isInitFinished = initSegmentor();
    }

    @Async
    public Future<Boolean> initSegmentor() {
        if (segmenter == null) {
            return new AsyncResult<>(false);
        }
        segmenter.init();
        return new AsyncResult<>(true);
    }

    @Override
    public Question getQuestionById(String questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (!question.isPresent()) {
            logger.info("question not found. questionId: {}", questionId);
            return null;
        }

        return question.orElse(null);
    }

    @Override
    public Question addQuestion(Question question) {
        if (!question.checkBaseInfo()) {
            logger.info("Question base info not set. Question info: {}", question.toString());
            return null;
        }

        Question questionResult = questionRepository.insert(question);
        if (questionResult == null) {
            logger.warn("insert question into db failed. Class info: {}", question.toString());
        }
        return questionResult;
    }

    @Override
    public Question modQuestion(Question question) {
        if (!question.checkBaseInfo()) {
            logger.info("Question base info not set. Question info: {}", question.toString());
            return null;
        }

        if (!questionRepository.findById(question.getId()).isPresent()) {
            logger.info("Question not found. Question info: {}", question.toString());
            return null;
        }

        Question result = questionRepository.save(question);
        if (result == null) {
            logger.warn("insert question into db failed. Class info: {}", question.toString());
        }
        return result;
    }

    @Override
    public boolean delQuestion(Question question) {
        questionRepository.deleteById(question.getId());
        return true;
    }

    @Override
    public double compareWithAnswer(Question question, String answer) {
        List<String> standardWords = segmenter.participle(question.getAnswer());
        List<String> studentWords = segmenter.participle(answer);

        logger.info("Standard answer words: " + standardWords.toString());
        logger.info("Student answer words: " + studentWords.toString());


        Map<String, Integer> standardVector = new HashMap<>();
        Map<String, Integer> studentVector = new HashMap<>();

        for (String word : standardWords) {
            if (!standardVector.containsKey(word)) {
                standardVector.put(word, 0);
            }
            if (!studentVector.containsKey(word)) {
                studentVector.put(word, 0);
            }
        }

        for (String word : standardWords) {
            standardVector.put(word, standardVector.get(word) + 1);
        }

        for (String word : studentWords) {
            if (studentVector.containsKey(word)) {
                studentVector.put(word, studentVector.get(word) + 1);
            }
        }

        double dotSum = .0;
        double standardLength = .0;
        double studentLength = .0;
        for (String word : standardVector.keySet()) {
            dotSum += standardVector.get(word) * studentVector.get(word);
            standardLength += standardVector.get(word) * standardVector.get(word);
            studentLength += studentVector.get(word) * studentVector.get(word);
        }

        return dotSum / (Math.sqrt(standardLength) * Math.sqrt(studentLength));
    }
}
