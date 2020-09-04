package com.smg.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smg.Pojo.TextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Post {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(Post.class);

    public static void main() throws Exception {
        String text = "企业按照树状结构进行展示和管理，实现层级的管理";
        String base64Str = Base64Tool.fileToBase64(text);
        String PcmMD5FileName = "test1";
        Integer spd = 0;
        String time = System.currentTimeMillis() + "";
        String salt = "C6K02DUeJct3VGn7";
        String ed = PcmMD5FileName + spd + time;
        String key = Md5.md5(ed, salt);

        TextInfo textInfo = new TextInfo();
        textInfo.setText(base64Str);
        textInfo.setPcmMD5FileName(PcmMD5FileName);
        textInfo.setSpd(spd);
        textInfo.setDate(time);
        textInfo.setKey(key);


        String json = objectMapper.writeValueAsString(textInfo);
        String res = sendPost("http://localhost:8080/textToRedio", json);
        System.out.println(res);
    }

    public static String sendPost(String url, String json) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置下面的属性
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //设置请求属性
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(json);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
            //将返回结果转换为字符串
        } catch (Exception e) {
            throw new RuntimeException("远程通路异常" + e.toString());
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
        return result;
    }
}
