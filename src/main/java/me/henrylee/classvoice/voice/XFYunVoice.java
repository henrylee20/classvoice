package me.henrylee.classvoice.voice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class XFYunVoice implements VoiceOperator {

    private static XFYunVoice instance = new XFYunVoice();

    public static XFYunVoice getInstance() {
        return instance;
    }


    private XFYunVoice() {
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private Map<String, String> GetHeader(String param, String apiKey) {
        Map<String, String> headers = new HashMap<>();
        Logger logger = LoggerFactory.getLogger(XFYunVoice.class);

        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5 Digest Error");
            return null;
        }

        try {
            String xTime = URLEncoder.encode(String.format("%d", System.currentTimeMillis() / 1000), "UTF-8");
            String xParam = (Base64Utils.encodeToString(param.getBytes()));
            String xChecksum = byteArrayToHexString(md.digest((apiKey + xTime + xParam).getBytes()));

            headers.put("X-CurTime", xTime);
            headers.put("X-Param", xParam);
            headers.put("X-CheckSum", xChecksum);

        } catch (UnsupportedEncodingException e) {
            logger.warn("Error: " + e.getMessage());
        }

        return headers;
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

    @Override
    public String ASR(String filename) {
        String voiceParam =
                "{" +
                        "\"engine_type\": \"sms16k\"," +
                        "\"aue\": \"raw\"" +
                        "}";


        String apiKey = "96f0dd252f2fcb85c6819ba27be34051";
        Map<String, String> headers = this.GetHeader(voiceParam, apiKey);

        if (headers == null) {
            logger.error("Cannot get headers");
            return "";
        }

        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://voice.henrylee.me/iat");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            for (String key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }
        } catch (ProtocolException e) {
            logger.error("Protocol error: " + e.getMessage());
            return "";
        } catch (MalformedURLException e) {
            logger.error("URL error: " + e.getMessage());
            return "";
        } catch (IOException e) {
            logger.error("Connect IO error: " + e.getMessage());
            return "";
        }

        byte[] voice = readVoiceData(filename);

        int code = 0;
        String codeInfo = null;
        int counter = 1;
        try {
            do {

                connection.connect();

                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                String data = "audio=" + Base64Utils.encodeToString(voice);
                output.writeBytes(data);
                output.flush();
                output.close();

                code = connection.getResponseCode();
                codeInfo = connection.getResponseMessage();

                if (code != 200) {
                    logger.warn(String.format("ASR Failed, retry. Stat code: %d, Message: %s", code, codeInfo));
                }
                counter--;
            } while (code != 200 && counter > 0);
        } catch (IOException e) {
            logger.error("ASR communicate with server error: " + e.getMessage());
            return "";
        }

        if (code != 200) {
            logger.warn(String.format("ASR Failed. Stat code: %d, Message: %s", code, codeInfo));
            return "";
        }

        StringBuilder result = new StringBuilder();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (IOException e) {
            logger.error("Cannot get data: " + e.getMessage());
        }

        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> data_map = parser.parseMap(result.toString());

        return data_map.get("data").toString();
    }
}
