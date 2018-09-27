package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.VoiceInfo;

public interface VoiceInfoService {
    VoiceInfo saveVoice(byte[] voice, String studentId, String questionId);

    byte[] getMP3Voice(String voiceId);

    VoiceInfo feedback(String voiceId, String feedback);

    VoiceInfo getVoiceInfo(String voiceId);
}
