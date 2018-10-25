package me.henrylee.classvoice.service;

import me.henrylee.classvoice.nlp.Segmenter;
import me.henrylee.classvoice.nlp.SegmenterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

@Service
public class NLPService {

    @Value("${henrylee.nlp.className}")
    String segmenterClassName;

    private Segmenter segmenter;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public NLPService() {
    }

    @Async("NLPTaskPool")
    public Future<Boolean> initNLP() {
        this.segmenter = SegmenterFactory.getWordSeg(this.segmenterClassName);
        logger.info("NLP segmenter init begin");
        this.segmenter.init();
        logger.info("NLP segmenter init finish");
        return new AsyncResult<>(true);
    }

    @Async("NLPTaskPool")
    public Future<List<String>> getWords(String sentence) {
        List<String> result = this.segmenter.participle(sentence);
        return new AsyncResult<>(result);
    }

}
