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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.Future;

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
    }


    private boolean mp3Transcoding(String mp3Filename, String wavFilename) {
        try {
            String[] cmd = {"ffmpeg", "-i", mp3Filename,
                    "-f", "wav", "-ar", "8000", "-acodec", "pcm_s16le", wavFilename, "-y"};

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

    private boolean saveMp3File(byte[] voice, VoiceInfo voiceInfo) {

        try {
            File mp3File = new File(voiceInfo.getPath());

            // create directory which not exist
            File dir = mp3File.getParentFile();
            if (!(dir.exists() && dir.isDirectory())) {
                if (!dir.mkdirs()) {
                    logger.error("cannot create dir to save voices. dir: {}", dir.getPath());
                    return false;
                }
            }

            FileOutputStream stream = new FileOutputStream(mp3File);
            stream.write(voice);
            stream.close();
        } catch (IOException e) {
            // TODO cannot write file
            logger.warn(e.getMessage());
            voiceInfo.setPath("");
            return false;
        }

        logger.info("Saved mp3, path: " + voiceInfo.getPath());
        return true;
    }

    @Override
    public VoiceInfo saveVoice(byte[] voice, VoiceInfo baseInfo) {

        List<VoiceInfo> voices = voiceInfoRepository.findByStudentId(baseInfo.getStudentId());
        for (VoiceInfo info : voices) {
            if (info.getQuestionId().equals(baseInfo.getQuestionId())) {
                return null;
            }
        }

        VoiceInfo voiceInfo = baseInfo;

        voiceInfo = voiceInfoRepository.insert(voiceInfo);


        voiceInfo.setPath(String.format("%s/voice.%s.mp3", this.voiceRoot, voiceInfo.getId()));
        if (!saveMp3File(voice, voiceInfo)) {
            voiceInfoRepository.deleteById(voiceInfo.getId());
            return null;
        }

        voiceInfoRepository.save(voiceInfo);

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

    @Async("ASRTaskPool")
    @Override
    public Future<String> ASR(VoiceInfo voiceInfo) {
        String wav_path;
        wav_path = voiceInfo.getPath().substring(0, voiceInfo.getPath().lastIndexOf(".")) + ".wav";

        File wavFile = new File(wav_path);
        if (!wavFile.exists()) {
            if (!mp3Transcoding(voiceInfo.getPath(), wav_path)) {
                logger.error("cannot transcoding file. file: [{}] -> [{}]", voiceInfo.getPath(), wav_path);
                return new AsyncResult<>(voiceInfo.getContent());
            }
        }

        // ASR voice to text
        logger.info("ASR start. voiceId: {}", voiceInfo.getId());
        VoiceOperator voiceOperator = VoiceOperatorFactory.getVoiceOperator(this.asrClassName);
        if (voiceOperator == null) {
            logger.error("Cannot get ASR class");
        } else {
            String result = voiceOperator.ASR(voiceInfo.getPath());
            voiceInfo.setContent(result);
            voiceInfoRepository.save(voiceInfo);
            logger.info("ASR finish. voiceId: {}", voiceInfo.getId());
        }

        return new AsyncResult<>(voiceInfo.getContent());
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
