package me.henrylee.classvoice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class WXLogin {

    static private final String appId = "wx0aab07221bda852f";
    static private final String appSecret = "0abbfb24f250ce37d7347ef684c53893";

    public static WXUserInfo login(String code) throws Exception {
        Logger logger = LoggerFactory.getLogger(WXLogin.class);
        URL url = new URL(String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appId=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code));
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.connect();

        String response = getReturn(connection);

        logger.debug(response);

        if (response.contains("\"errcode\":")) {
            logger.warn("err! response: %s", response);
            return null;
        }

        int start;
        int end;

        start = response.indexOf("openid");
        start = response.indexOf(":", start);
        start = response.indexOf("\"", start) + 1;
        end = response.indexOf("\"", start);
        String openid = response.substring(start, end);

        start = response.indexOf("session_key");
        start = response.indexOf(":", start);
        start = response.indexOf("\"", start) + 1;
        end = response.indexOf("\"", start);
        String session_key = response.substring(start, end);

        logger.debug(String.format("openid: %s, session_key: %s", openid, session_key));

        WXUserInfo result = new WXUserInfo();
        result.setOpenid(openid);
        result.setSessionKey(session_key);

        return result;
    }

    private static String getReturn(HttpsURLConnection connection) throws IOException {
        StringBuilder buffer = new StringBuilder();

        InputStream stream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        String str = null;

        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        return buffer.toString();
    }
}
