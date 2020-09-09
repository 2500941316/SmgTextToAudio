package com.smg.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smg.exceptions.BusinessException;
import com.smg.pojo.Constance;
import com.smg.pojo.TextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(PostUtils.class);

    private PostUtils() {
        throw new IllegalStateException("PostUtils class");
    }

    public static void threadsMain() {
        String text = "程序用编程语言来写程序，最终开发的结果就是一个软件。就像大家都知道的QQ，腾讯视频，酷狗音乐等一系列软件。这些软件要想运行必须得有系统控制它吧。当然，有人会问：为什么要用操作系统呢？当然，很久以前的那些程序员确实是在没有操作环境下，编程语言是操作硬件来编写的。你可能觉得没问题，但是其实问题很严重。如果一直像以前那样会严重影响效率的";
        final String base64Str = Base64Tool.fileToBase64(text);
        final int spd = 0;
        final String time = System.currentTimeMillis() + "";
        final String salt = "C6K02DUeJct3VGn7";
        final String vid = "60030";
        final String vol = "5";
        final ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "创建了");
                    String pcmMD5FileName = Thread.currentThread().getName() + System.currentTimeMillis() + ".pcm";
                    String ed = pcmMD5FileName + spd + time;
                    String key = Md5Utils.md5(ed, salt);

                    TextInfo textInfo = new TextInfo();
                    textInfo.setText(base64Str);
                    textInfo.setVid(vid);
                    textInfo.setVol(vol);
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
                    logger.info("文件下载地址为：" + res);
                }
            });
        }
    }

    public static void shellFfmpeg() {
        final ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    logger.info("新线程开启" + Thread.currentThread());
                    pcmToMp3(Constance.PCMPATH + "java.pcm");

                    logger.info("转码结束");
                }
            });
        }
    }


    public static String sendPost(String url, String json) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
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

    public static boolean pcmToMp3(String pcmFile) {
        //先获取mp3对应的文件名称
        String mp3FileNane = pcmFile.substring(0, pcmFile.lastIndexOf('.')) + Thread.currentThread().getName() + System.currentTimeMillis() + ".mp3";
        logger.info("mp3生成地址：" + mp3FileNane);
        String pcmToMp3 = "ffmpeg -y -f s16be -ac 1 -ar 16000 -acodec pcm_s16le -i " + pcmFile + " " + mp3FileNane;
        Process process = null;
        try {
            logger.info("开始启动转码");
            process = Runtime.getRuntime().exec(pcmToMp3);
            logger.info("转码命令生成成功");

            if (null == process) {
                return false;
            }
            process.waitFor();
            try (
                    InputStream errorStream = process.getErrorStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
                    BufferedReader br = new BufferedReader(inputStreamReader)) {
                String line = null;
                StringBuffer context = new StringBuffer();
                logger.info("流对象生成");
                while ((line = br.readLine()) != null) {
                    context.append(line);
                }
                logger.info("pcm读取成功");
            } catch (IOException e) {
                logger.error("转码失败");
                return false;
            } finally {
                process.destroy();
            }
            return true;
        } catch (Exception e) {
            logger.error("转码失败");
            return false;
        }
    }
}
