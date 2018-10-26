package me.henrylee.classvoice.nlp;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.corpus.Bigram;
import org.apdplat.word.segmentation.PartOfSpeech;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.PartOfSpeechTagging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordSeg implements Segmenter {
    private static WordSeg instance = new WordSeg();

    public static WordSeg getInstance() {
        return instance;
    }

    private WordSeg() {

    }

    private boolean isInited = false;
    private boolean isIniting = false;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Override
    public double sentenceScore(String sentence) {
        String[] sentences = sentence.split("[。，]");
        List<Word> words = WordSegmenter.segWithStopWords(sentence);
        PartOfSpeechTagging.process(words);
        words.removeIf(w -> w.getPartOfSpeech().getPos().equals("e") || w.getPartOfSpeech().getPos().equals("y"));
        double words_num = words.size();

        double result = 0;
        for (String one_sentence : sentences) {

            words = WordSegmenter.segWithStopWords(one_sentence);
            PartOfSpeechTagging.process(words);
            words.removeIf(w -> w.getPartOfSpeech().getPos().equals("e") || w.getPartOfSpeech().getPos().equals("y"));

            StringBuilder words_text = new StringBuilder();
            for (Word word : words) {
                words_text.append(word.getText()).append("[").append(word.getPartOfSpeech().getPos()).append("], ");
            }
            logger.info("words: {}", words_text.toString());

            if (words.size() > 1) {
                double total = words.size() - 1;
                double match = 0;

                for (int i = 0; i < words.size() - 1; i++) {
                    logger.info("word [{}] and [{}]: {}", words.get(i).getText(), words.get(i + 1).getText(),
                            Bigram.getScore(words.get(i).getText(), words.get(i + 1).getText()));
                    if (Bigram.getScore(words.get(i).getText(), words.get(i + 1).getText()) > 0) {
                        match++;
                    }
                }
                logger.info("match: {}, total: {}", match, total);
                result += (match / total) * ((total + 1) / words_num);
            } else if (words.size() == 1) {
                result += 1 / words_num;
            } else {
                result += 0;
            }
        }

        return result;
    }
}
