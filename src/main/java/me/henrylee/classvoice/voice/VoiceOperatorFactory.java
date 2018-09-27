package me.henrylee.classvoice.voice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VoiceOperatorFactory {
    public static VoiceOperator getVoiceOperator(String name) {

        Class<?> clazz;
        Method method;
        VoiceOperator result;
        try {
            clazz = Class.forName(name);
            method = clazz.getMethod("getInstance");
            result = (VoiceOperator) method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }

        return result;
    }
}
