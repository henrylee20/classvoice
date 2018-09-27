package me.henrylee.classvoice.voice;

import com.baidu.aip.speech.AipSpeech;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class BaiduVoice implements VoiceOperator {
    // Single Instance
    private static BaiduVoice instance = new BaiduVoice();

    public static BaiduVoice getInstance() {
        return instance;
    }

    private AipSpeech client;

    private BaiduVoice() {
        client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
    }

    private static final String APP_ID = "11540693";
    private static final String API_KEY = "Abz1KKI39kcnQicu9tQXZH5c";
    private static final String SECRET_KEY = "ekqsjlODUy0gAVuhAH5p2WEr5qEkuGYM";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String ASR(String filename) {
        HashMap<String, Object> options = new HashMap<>();
        options.put("dev_pid", 1537);
        JSONObject result = client.asr(filename, "wav", 8000, options);
        if (result.getInt("err_no") != 0) {
            logger.warn("ASR error: " + result.toString());
            return "";
        }
        return result.getJSONArray("result").getString(0);
    }
}
