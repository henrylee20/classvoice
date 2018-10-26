package me.henrylee.classvoice.nlp;

import java.util.List;

public interface Segmenter {
    void init();

    List<String> participle(String sentence);

    double sentenceScore(String sentence);
}
