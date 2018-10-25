package me.henrylee.classvoice.nlp;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WordSeg implements Segmenter {
    private static WordSeg instance = new WordSeg();

    public static WordSeg getInstance() {
        return instance;
    }

    private WordSeg() {

    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isInited = false;
    private boolean isIniting = false;

    @Override
    public void init() {
        if (isInited)
            return;

        synchronized (this) {
            if (isIniting)
                return;

            isIniting = true;
        }

        WordSegmenter.seg("hello world");
        isInited = true;
    }

    @Override
    public List<String> participle(String sentence) {

        if (!isInited)
            init();

        while (!isInited) {
            try {
                logger.info("waiting for init.");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error("Sleep error. msg: {}", e.getMessage());
            }
        }

        List<Word> words = WordSegmenter.seg(sentence);
        List<String> result = new ArrayList<>();

        for (Word word : words) {
            result.add(word.getText());
        }

        return result;
    }
}
