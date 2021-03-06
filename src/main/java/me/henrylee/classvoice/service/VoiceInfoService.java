package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.VoiceInfo;

import java.util.concurrent.Future;

public interface VoiceInfoService {
    VoiceInfo saveVoice(byte[] voice, VoiceInfo baseInfo);

    byte[] getMP3Voice(String voiceId);

    Future<String> ASR(VoiceInfo voiceInfo);

    VoiceInfo feedback(String voiceId, String feedback);

    VoiceInfo getVoiceInfo(String voiceId);
}
