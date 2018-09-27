package me.henrylee.classvoice.utils;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import java.util.ArrayList;
import java.util.List;

public class WordCut {

    public static List<String> wordCut(String text) {
        List<Word> words = WordSegmenter.seg(text);
        //List<Word> words = WordSegmenter.segWithStopWords(text);
        List<String> result = new ArrayList<>();

        for (Word word : words) {
            result.add(word.getText());
        }

        return result;
    }

}
