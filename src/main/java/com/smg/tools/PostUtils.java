package com.smg.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smg.exceptions.BusinessException;
import com.smg.pojo.TextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(PostUtils.class);

    private PostUtils() {
        throw new IllegalStateException("PostUtils class");
    }

    public static void main()  {
        String text = "程序用编程语言来写程序，最终开发的结果就是一个软件。就像大家都知道的QQ，腾讯视频，酷狗音乐等一系列软件。这些软件要想运行必须得有系统控制它吧。当然，有人会问：为什么要用操作系统呢？当然，很久以前的那些程序员确实是在没有操作环境下，编程语言是操作硬件来编写的。你可能觉得没问题，但是其实问题很严重。如果一直像以前那样会严重影响效率的。操作系统是出现在硬件之上的，是用来控制硬件的。所以，我们开发时只需要调用操作系统为我们提供的简单的接口就可以了";
        String base64Str = Base64Tool.fileToBase64(text);
        String pcmMD5FileName = "java常用知识.pcm";
        Integer spd = 0;
        String time = System.currentTimeMillis() + "";
        String salt = "C6K02DUeJct3VGn7";
        String ed = pcmMD5FileName + spd + time;
        String key = Md5Utils.md5(ed, salt);

        TextInfo textInfo = new TextInfo();
        textInfo.setText(base64Str);
        textInfo.setPcmMD5FileName(pcmMD5FileName);
        textInfo.setSpd(spd);
        textInfo.setDate(time);
        textInfo.setKey(key);


        String json = null;
        try {
            json = objectMapper.writeValueAsString(textInfo);
        } catch (JsonProcessingException e) {
           logger.error(e.getMessage());
        }

        String res = sendPost("http://localhost:8080/textToRedio", json);
       logger.info("请求输出："+res);

    }

    public static String sendPost(String url, String json) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result=new StringBuilder();
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
                result.append(line);
            }
            //将返回结果转换为字符串
        } catch (Exception e) {
            throw new BusinessException("远程通路异常" + e.toString());
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
        return result.toString();
    }
}
