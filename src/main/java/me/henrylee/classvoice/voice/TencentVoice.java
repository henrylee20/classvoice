package me.henrylee.classvoice.voice;

import com.tencentcloudapi.aai.v20180522.AaiClient;
import com.tencentcloudapi.aai.v20180522.models.SentenceRecognitionRequest;
import com.tencentcloudapi.aai.v20180522.models.SentenceRecognitionResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TencentVoice implements VoiceOperator {
    // Single Instance
    private static TencentVoice instance = new TencentVoice();

    public static TencentVoice getInstance() {
        return instance;
    }

    private AaiClient aaiClient;

    private TencentVoice() {
        Credential credential = new Credential(secretId, secretKey);
        aaiClient = new AaiClient(credential, region);
    }

    private static final String secretId = "AKID3ufEbjuKpAJF1alA6pLdmeMuEJAn9KFv";
    private static final String secretKey = "by70dz5wXsxnNhla9UbT5QVKuEI6fy1o";
    private static final String region = "";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String ASR(String filename) {
        SentenceRecognitionRequest request = new SentenceRecognitionRequest();
        request.setSubServiceType(2);
        request.setEngSerViceType("8k");
        request.setSourceType(1);
        request.setVoiceFormat("wav");
        request.setProjectId(0);
        request.setUsrAudioKey(filename);

        byte[] data = readVoiceData(filename);
        int dataLen = data.length;

        if (data == null) {
            logger.warn("cannot read wav file. filename: {}", filename);
            return "";
        }

        String dataBase64 = Base64Utils.encodeToString(data);

        request.setDataLen(dataLen);
        request.setData(dataBase64);

        SentenceRecognitionResponse response = null;
        try {
            response = aaiClient.SentenceRecognition(request);
        } catch (TencentCloudSDKException e) {
            logger.warn("ASR SDK failed. msg: {}", e.getMessage());
            return "waiting for ASR";
        }

        if (response == null) {
            logger.warn("ASR failed. returned nothing");
            return "";
        }

        return response.getResult();
    }

    private byte[] readVoiceData(String filename) {
        File file = new File(filename);
        Long file_length = file.length();
        byte[] voice = new byte[file_length.intValue()];
        try {
            FileInputStream stream = new FileInputStream(file);
            stream.read(voice);
            stream.close();
        } catch (IOException e) {
            logger.warn("wav file read err");
            return null;
        }
        return voice;
    }
}
