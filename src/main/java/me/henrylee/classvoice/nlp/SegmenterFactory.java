package me.henrylee.classvoice.nlp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SegmenterFactory {
    public static Segmenter getWordSeg(String className) {
        Class<?> clazz;
        Method method;
        Segmenter result;

        try {
            clazz = Class.forName(className);
            method = clazz.getMethod("getInstance");
            result = (Segmenter) method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            return null;
        }

        return result;
    }
}
