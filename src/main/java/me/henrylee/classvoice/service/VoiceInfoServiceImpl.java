package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.VoiceInfo;
import me.henrylee.classvoice.model.VoiceInfoRepository;
import me.henrylee.classvoice.voice.VoiceOperator;
import me.henrylee.classvoice.voice.VoiceOperatorFactory;
import org.apdplat.word.WordSegmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class VoiceInfoServiceImpl implements VoiceInfoService {

    @Value("${henrylee.asr.className}")
    private String asrClassName;

    @Value("${henrylee.classvoice.root}")
    private String voiceRoot;

    private VoiceInfoRepository voiceInfoRepository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public VoiceInfoServiceImpl(VoiceInfoRepository voiceInfoRepository) {
        this.voiceInfoRepository = voiceInfoRepository;
        // TODO Word segmenter init
        // WordSegmenter.seg("Hello");
    }

    private boolean prepareWavFile(byte[] voice, VoiceInfo voiceInfo) {

        String mp3_path = String.format("/tmp/tmp.%s.%s.mp3", voiceInfo.getStudentId(), voiceInfo.getQuestionId());
        mp3_path = voiceInfo.getPath().substring(0, voiceInfo.getPath().lastIndexOf(".")) + ".mp3";

        try {
            // TODO create directory which not exist
            FileOutputStream stream = new FileOutputStream(mp3_path);
            stream.write(voice);
            stream.close();
        } catch (IOException e) {
            // TODO cannot write file
            logger.warn(e.getMessage());
            voiceInfo.setPath("");
            return false;
        }

        logger.info("Saved mp3, path: " + mp3_path);

        // encode mp3 to wav
        String path = voiceInfo.getPath();

        try {
            String[] cmd = {"ffmpeg", "-i", mp3_path, "-f", "wav", "-ar", "8000", "-acodec", "pcm_s16le", path, "-y"};

            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process p = pb.start();

            int ret = p.waitFor();
            int exit_code = p.exitValue();

            if (exit_code != 0) {
                logger.warn(String.format("transcode err, exit_code: %d, ret: %d", exit_code, ret));
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String pinfo = null;
                while ((pinfo = reader.readLine()) != null) {
                    logger.warn("Process: " + pinfo);
                }
            } else {
                logger.info(String.format("transcode completed, exit_code: %d", exit_code));
            }

        } catch (IOException e) {
            logger.warn("transcode failed");
            return false;
        } catch (InterruptedException e) {
            logger.warn("transcode running failed");
            return false;
        }

        return true;
    }

    @Override
    public VoiceInfo saveVoice(byte[] voice, String studentId, String questionId) {
        VoiceInfo voiceInfo = new VoiceInfo();
        voiceInfo.setStudentId(studentId);
        voiceInfo.setQuestionId(questionId);
        voiceInfo.setPath(String.format("%s/voice.%s.%s.wav", this.voiceRoot, studentId, questionId)); // TODO voice save root path

        if (!prepareWavFile(voice, voiceInfo)) {
            return null;
        }

        // ASR voice to text
        VoiceOperator voiceOperator = VoiceOperatorFactory.getVoiceOperator(this.asrClassName);
        if (voiceOperator == null) {
            logger.error("Cannot get ASR class");
            return null;
        }
        String result = voiceOperator.ASR(voiceInfo.getPath());
        voiceInfo.setContent(result);

        voiceInfoRepository.insert(voiceInfo);

        return voiceInfo;
    }

    @Override
    public byte[] getMP3Voice(String voiceId) {
        Optional<VoiceInfo> voiceInfo = voiceInfoRepository.findById(voiceId);
        if (!voiceInfo.isPresent()) {
            logger.info("cannot load voice. voiceId: {}", voiceId);
            return null;
        }

        String mp3_path = voiceInfo.get().getPath();
        mp3_path = mp3_path.substring(0, mp3_path.lastIndexOf(".")) + ".mp3";

        File file = new File(mp3_path);
        Long fileLen = file.length();

        byte[] data = new byte[fileLen.intValue()];
        try {
            FileInputStream stream = new FileInputStream(file);
            stream.read(data);
            stream.close();
        } catch (IOException e) {
            logger.warn("cannot read mp3 file. msg: {}", e.getMessage());
            return null;
        }
        return data;
    }

    @Override
    public VoiceInfo feedback(String voiceId, String feedback) {
        Optional<VoiceInfo> voiceInfo = voiceInfoRepository.findById(voiceId);

        if (!voiceInfo.isPresent()) {
            logger.info("voice not found. voiceId: {}", voiceId);
            return null;
        }

        voiceInfo.get().setFeedback(feedback);
        return voiceInfoRepository.save(voiceInfo.get());
    }

    @Override
    public VoiceInfo getVoiceInfo(String voiceId) {
        Optional<VoiceInfo> voiceInfo = voiceInfoRepository.findById(voiceId);
        return voiceInfo.orElse(null);
    }
}
